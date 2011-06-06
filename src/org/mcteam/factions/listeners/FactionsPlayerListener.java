package org.mcteam.factions.listeners;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.mcteam.factions.Board;
import org.mcteam.factions.Conf;
import org.mcteam.factions.FLocation;
import org.mcteam.factions.FPlayer;
import org.mcteam.factions.Faction;
import org.mcteam.factions.Factions;
import org.mcteam.factions.util.TextUtil;



public class FactionsPlayerListener extends PlayerListener{

	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		if ((event.getMessage().startsWith(Factions.instance.getBaseCommand()+" ") || event.getMessage().equals(Factions.instance.getBaseCommand())) && Conf.allowNoSlashCommand) {
			List<String> parameters = TextUtil.split(event.getMessage().trim());
			parameters.remove(0);
			CommandSender sender = event.getPlayer();			
			Factions.instance.handleCommand(sender, parameters);
			event.setCancelled(true);
			return;
		}
		
		if (event.isCancelled()) {
			return;
		}
		
		Player talkingPlayer = event.getPlayer();
		String msg = event.getMessage();
		
		// ... it was not a command. This means that it is a chat message!
		FPlayer me = FPlayer.get(talkingPlayer);
		
		// Is it a faction chat message?
		if (me.isFactionChatting()) {
			String message = String.format(Conf.factionChatFormat, me.getNameAndRelevant(me), msg);
			me.getFaction().sendMessage(message);
			Logger.getLogger("Minecraft").info("FactionChat "+me.getFaction().getTag()+": "+message);
			event.setCancelled(true);
			return;
		}
		
		// Are we to insert the Faction tag into the format?
		// If we are not to insert it - we are done.
		if ( ! Conf.chatTagEnabled) {
			return;
		}

		int InsertIndex = 0;
		String eventFormat = event.getFormat();
		
		if (!Conf.chatTagReplaceString.isEmpty() && eventFormat.contains(Conf.chatTagReplaceString)) {
			// we're using the "replace" method of inserting the faction tags
			InsertIndex = eventFormat.indexOf(Conf.chatTagReplaceString);
			eventFormat = eventFormat.replace(Conf.chatTagReplaceString, "");
			Conf.chatTagPadAfter = false;
			Conf.chatTagPadBefore = false;
		}
		else if (!Conf.chatTagInsertAfterString.isEmpty() && eventFormat.contains(Conf.chatTagInsertAfterString)) {
			// we're using the "insert after string" method
			InsertIndex = eventFormat.indexOf(Conf.chatTagInsertAfterString) + Conf.chatTagInsertAfterString.length();
		}
		else if (!Conf.chatTagInsertBeforeString.isEmpty() && eventFormat.contains(Conf.chatTagInsertBeforeString)) {
			// we're using the "insert before string" method
			InsertIndex = eventFormat.indexOf(Conf.chatTagInsertBeforeString);
		}
		else {
			// we'll fall back to using the index place method
			InsertIndex = Conf.chatTagInsertIndex;
			if (InsertIndex > eventFormat.length())
				return;
		}
		
		String formatStart = eventFormat.substring(0, InsertIndex) + ((Conf.chatTagPadBefore && !me.getChatTag().isEmpty()) ? " " : "");
		String formatEnd = ((Conf.chatTagPadAfter && !me.getChatTag().isEmpty()) ? " " : "") + eventFormat.substring(InsertIndex);
		
		String nonColoredMsgFormat = formatStart + me.getChatTag().trim() + formatEnd;
		
		// Relation Colored?
		if (Conf.chatTagRelationColored) {
			// We must choke the standard message and send out individual messages to all players
			// Why? Because the relations will differ.
			event.setCancelled(true);
			
			for (Player listeningPlayer : Factions.instance.getServer().getOnlinePlayers()) {
				FPlayer you = FPlayer.get(listeningPlayer);
				String yourFormat = formatStart + me.getChatTag(you).trim() + formatEnd;
				listeningPlayer.sendMessage(String.format(yourFormat, talkingPlayer.getDisplayName(), msg));
			}
			
			// Write to the log... We will write the non colored message.
			String nonColoredMsg = ChatColor.stripColor(String.format(nonColoredMsgFormat, talkingPlayer.getDisplayName(), msg));
			Logger.getLogger("Minecraft").info(nonColoredMsg);
		} else {
			// No relation color.
			event.setFormat(nonColoredMsgFormat);
		}
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		// Make sure that all online players do have a fplayer.
		FPlayer me = FPlayer.get(event.getPlayer());
		
		// Update the lastLoginTime for this fplayer
		me.setLastLoginTime(System.currentTimeMillis());
		
		// Run the member auto kick routine. Twice to get to the admins...
		FPlayer.autoLeaveOnInactivityRoutine();
		FPlayer.autoLeaveOnInactivityRoutine();
	}
	
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
		// Make sure player's power is up to date when they log off.
		FPlayer me = FPlayer.get(event.getPlayer());
		me.getPower();
	}
	
	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		FPlayer me = FPlayer.get(event.getPlayer());
		
		// Did we change coord?
		FLocation from = me.getLastStoodAt();
		FLocation to = new FLocation(event.getTo());
		
		if (from.equals(to)) {
			return;
		}
		
		// Yes we did change coord (:
		
		me.setLastStoodAt(to);
		
		if (me.isMapAutoUpdating()) {
			me.sendMessage(Board.getMap(me.getFaction(), to, me.getPlayer().getLocation().getYaw()));
		} else {
			// Did we change "host"(faction)?
			Faction factionFrom = Board.getFactionAt(from);
			Faction factionTo = Board.getFactionAt(to);
			if ( factionFrom != factionTo) {
				me.sendFactionHereMessage();
			}
		}
	}

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Block block = event.getClickedBlock();
		Player player = event.getPlayer();

		if (block == null) {
			return;  // clicked in air, apparently
		}

		if ( ! canPlayerUseBlock(player, block)) {
			event.setCancelled(true);
			return;
		}

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;  // only interested on right-clicks for below
		}

		// this check below might no longer be needed... bucket detection is now necessarily handled separately in onPlayerBucketXXX() events, and
		// Flint&Steel is somehow detected before this in onBlockPlace(), and that's currently it for the default territoryDenyUseageMaterials
		if ( ! this.playerCanUseItemHere(player, block, event.getMaterial())) {
			event.setCancelled(true);
			return;
		}
	}

	public boolean playerCanUseItemHere(Player player, Block block, Material material) {

		if (Conf.adminBypassPlayers.contains(player.getName())) {
			return true;
		}

		if ( ! Conf.territoryDenyUseageMaterials.contains(material)) {
			return true; // Item isn't one we're preventing.
		}

		Faction otherFaction = Board.getFactionAt(new FLocation(block));

		FPlayer me = FPlayer.get(player);

		if (otherFaction.isNone()) {
			if (!Conf.wildernessDenyUseage || Factions.hasPermAdminBypass(player)) {
				return true; // This is not faction territory. Use whatever you like here.
			}
			me.sendMessage("You can't use "+TextUtil.getMaterialName(material)+" in the wilderness.");
			return false;
		}
		
		if (otherFaction.isSafeZone() && Conf.safeZoneDenyUseage) {
			if (Factions.hasPermManageSafeZone(player)) {
				return true;
			}
			me.sendMessage("You can't use "+TextUtil.getMaterialName(material)+" in a safe zone.");
			return false;
		}
		else if (otherFaction.isWarZone() && Conf.warZoneDenyUseage) {
			if (Factions.hasPermManageWarZone(player)) {
				return true;
			}
			me.sendMessage("You can't use "+TextUtil.getMaterialName(material)+" in a war zone.");
			return false;
		}
		
		Faction myFaction = me.getFaction();

		// Cancel if we are not in our own territory
		if (myFaction != otherFaction && Conf.territoryDenyUseage) {
			me.sendMessage("You can't use "+TextUtil.getMaterialName(material)+" in the territory of "+otherFaction.getTag(myFaction));
			return false;
		}

		return true;
	}

	public boolean canPlayerUseBlock(Player player, Block block) {

		if (Conf.adminBypassPlayers.contains(player.getName())) {
			return true;
		}

		Material material = block.getType();

		// We only care about some material types.
		if ( ! Conf.territoryProtectedMaterials.contains(material)) {
			return true;
		}

		FPlayer me = FPlayer.get(player);
		Faction myFaction = me.getFaction();
		Faction otherFaction = Board.getFactionAt(new FLocation(block));

		// In safe zones you may use any block...
		if (otherFaction.isNormal() && myFaction != otherFaction) {
			me.sendMessage("You can't use "+TextUtil.getMaterialName(material)+" in the territory of "+otherFaction.getTag(myFaction));
			return false;
		}

		// You may use doors in both safeZone and wilderness
		return true;
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		FPlayer me = FPlayer.get(event.getPlayer());
		Location home = me.getFaction().getHome();
		if (Conf.homesEnabled && Conf.homesTeleportToOnDeath && home != null) {
			event.setRespawnLocation(home);
		}
	}

	// For some reason onPlayerInteract() sometimes misses bucket events depending on distance (something like 2-3 blocks away isn't detected),
	// but these separate bucket events below always fire without fail
	@Override
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if ( ! this.playerCanUseItemHere(player, block, event.getBucket())) {
			event.setCancelled(true);
			return;
		}
	}
	@Override
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if ( ! this.playerCanUseItemHere(player, block, event.getBucket())) {
			event.setCancelled(true);
			return;
		}
	}
}
