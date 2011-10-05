package com.massivecraft.factions.listeners;

import java.util.logging.Logger;
import java.util.Iterator;
import java.util.UnknownFormatConversionException;

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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Econ;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.SpoutFeatures;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.TextUtil;
import java.util.logging.Level;



public class FactionsPlayerListener extends PlayerListener{

	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Player talkingPlayer = event.getPlayer();
		String msg = event.getMessage();
		
		// ... it was not a command. This means that it is a chat message!
		FPlayer me = FPlayer.get(talkingPlayer);
		
		// Are we to insert the Faction tag into the format?
		// If we are not to insert it - we are done.
		if ( ! Conf.chatTagEnabled || Conf.chatTagHandledByAnotherPlugin) {
			return;
		}

		int InsertIndex = 0;
		String eventFormat = event.getFormat();
		
		if (!Conf.chatTagReplaceString.isEmpty() && eventFormat.contains(Conf.chatTagReplaceString)) {
			// we're using the "replace" method of inserting the faction tags
			// if they stuck "{FACTION_TITLE}" in there, go ahead and do it too
			if (eventFormat.contains("{FACTION_TITLE}")) {
				eventFormat = eventFormat.replace("{FACTION_TITLE}", me.getTitle());
			}
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
			
			for (Player listeningPlayer : event.getRecipients()) {
				FPlayer you = FPlayer.get(listeningPlayer);
				String yourFormat = formatStart + me.getChatTag(you).trim() + formatEnd;
				try {
					listeningPlayer.sendMessage(String.format(yourFormat, talkingPlayer.getDisplayName(), msg));
				}
				catch (UnknownFormatConversionException ex) {
					Factions.log(Level.SEVERE, "Critical error in chat message formatting! Complete format string: "+yourFormat);
					Factions.log(Level.SEVERE, "First half of event.getFormat() string: "+formatStart);
					Factions.log(Level.SEVERE, "Second half of event.getFormat() string: "+formatEnd);
					Factions.log(Level.SEVERE, "NOTE: To fix this quickly, running this command should work: f config chatTagInsertIndex 0");
					Factions.log(Level.SEVERE, "For a more proper fix, please read the chat configuration notes on the configuration page of the Factions user guide.");
					ex.printStackTrace();
					return;
				}
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
		final Player player = event.getPlayer();

		// Make sure that all online players do have a fplayer.
		FPlayer me = FPlayer.get(player);
		
		// Update the lastLoginTime for this fplayer
		me.setLastLoginTime(System.currentTimeMillis());
		
		// Run the member auto kick routine. Twice to get to the admins...
		FPlayer.autoLeaveOnInactivityRoutine();
		FPlayer.autoLeaveOnInactivityRoutine();

		// Appearance updates which are run when a player joins don't apply properly for other clients, so they need to be delayed slightly
		Factions.instance.getServer().getScheduler().scheduleSyncDelayedTask(Factions.instance, new Runnable() {
			public void run() {
				SpoutFeatures.updateAppearances(player);
			}
		});
	}
	
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
		// Make sure player's power is up to date when they log off.
		FPlayer me = FPlayer.get(event.getPlayer());
		me.getPower();
		Faction myFaction = me.getFaction();
		if (myFaction != null) {
			myFaction.memberLoggedOff();
		}
	}
	
	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		FPlayer me = FPlayer.get(player);
		
		// Did we change coord?
		FLocation from = me.getLastStoodAt();
		FLocation to = new FLocation(player.getLocation());
		
		if (from.equals(to)) {
			return;
		}
		
		// Yes we did change coord (:
		
		me.setLastStoodAt(to);
		
		if (me.isMapAutoUpdating()) {
			me.sendMessage(Board.getMap(me.getFaction(), to, player.getLocation().getYaw()));
		} else {
			// Did we change "host"(faction)?
			Faction factionFrom = Board.getFactionAt(from);
			Faction factionTo = Board.getFactionAt(to);
			Faction myFaction = me.getFaction();
			String ownersTo = myFaction.getOwnerListString(to);
			if (factionFrom != factionTo) {
				me.sendFactionHereMessage();
				if (Conf.ownedAreasEnabled && Conf.ownedMessageOnBorder && myFaction == factionTo && !ownersTo.isEmpty()) {
					me.sendMessage(Conf.ownedLandMessage+ownersTo);
				}
			}
			else if (Conf.ownedAreasEnabled && Conf.ownedMessageInsideTerritory && factionFrom == factionTo && myFaction == factionTo) {
				String ownersFrom = myFaction.getOwnerListString(from);
				if (Conf.ownedMessageByChunk || !ownersFrom.equals(ownersTo)) {
					if (!ownersTo.isEmpty()) {
						me.sendMessage(Conf.ownedLandMessage+ownersTo);
					}
					else if (!Conf.publicLandMessage.isEmpty()) {
						me.sendMessage(Conf.publicLandMessage);
					}
				}
			}
		}
		
		if (me.autoClaimEnabled()) {
			Faction myFaction = me.getFaction();
			Faction otherFaction = Board.getFactionAt(to);
			double cost = Econ.calculateClaimCost(myFaction.getLandRounded(), otherFaction.isNormal());

			if (me.getRole().value < Role.MODERATOR.value) {
				me.sendMessage("You must be "+Role.MODERATOR+" to claim land.");
				me.enableAutoClaim(false);
			}
			else if (Conf.worldsNoClaiming.contains(to.getWorldName())) {
				me.sendMessage("Sorry, this world has land claiming disabled.");
				me.enableAutoClaim(false);
			}
			else if (myFaction.getLandRounded() >= myFaction.getPowerRounded()) {
				me.sendMessage("You can't claim more land! You need more power!");
				me.enableAutoClaim(false);
			}
			else
				me.attemptClaim(false);
		}
		else if (me.autoSafeZoneEnabled()) {
			if (!Factions.hasPermManageSafeZone((CommandSender)player)) {
				me.enableAutoSafeZone(false);
			} else {
				FLocation playerFlocation = new FLocation(me);

				if (!Board.getFactionAt(playerFlocation).isSafeZone()) {
					Board.setFactionAt(Faction.getSafeZone(), playerFlocation);
					me.sendMessage("This land is now a safe zone.");
				}
			}
		}
		else if (me.autoWarZoneEnabled()) {
			if (!Factions.hasPermManageWarZone((CommandSender)player)) {
				me.enableAutoWarZone(false);
			} else {
				FLocation playerFlocation = new FLocation(me);

				if (!Board.getFactionAt(playerFlocation).isWarZone()) {
					Board.setFactionAt(Faction.getWarZone(), playerFlocation);
					me.sendMessage("This land is now a war zone.");
				}
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

		if ( ! canPlayerUseBlock(player, block, false)) {
			event.setCancelled(true);
			return;
		}

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;  // only interested on right-clicks for below
		}

		if ( ! this.playerCanUseItemHere(player, block.getLocation(), event.getMaterial(), false)) {
			event.setCancelled(true);
			return;
		}
	}

	public static boolean playerCanUseItemHere(Player player, Location location, Material material, boolean justCheck) {

		if (Conf.adminBypassPlayers.contains(player.getName())) {
			return true;
		}

		FLocation loc = new FLocation(location);
		Faction otherFaction = Board.getFactionAt(loc);

		if (otherFaction.hasPlayersOnline()){
			if ( ! Conf.territoryDenyUseageMaterials.contains(material)) {
				return true; // Item isn't one we're preventing for online factions.
			}
		}else{
			if ( ! Conf.territoryDenyUseageMaterialsWhenOffline.contains(material)) {
				return true; // Item isn't one we're preventing for offline factions.
			}
		}

		FPlayer me = FPlayer.get(player);

		if (otherFaction.isNone()) {
			if (!Conf.wildernessDenyUseage || Factions.hasPermAdminBypass(player) || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName())) {
				return true; // This is not faction territory. Use whatever you like here.
			}
			if (!justCheck) {
				me.sendMessage("You can't use "+TextUtil.getMaterialName(material)+" in the wilderness.");
			}
			return false;
		}
		else if (otherFaction.isSafeZone()) {
			if (!Conf.safeZoneDenyUseage || Factions.hasPermManageSafeZone(player)) {
				return true;
			}
			if (!justCheck) {
				me.sendMessage("You can't use "+TextUtil.getMaterialName(material)+" in a safe zone.");
			}
			return false;
		}
		else if (otherFaction.isWarZone()) {
			if (!Conf.warZoneDenyUseage || Factions.hasPermManageWarZone(player)) {
				return true;
			}
			if (!justCheck) {
				me.sendMessage("You can't use "+TextUtil.getMaterialName(material)+" in a war zone.");
			}
			return false;
		}
		
		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelation(otherFaction);
		boolean ownershipFail = Conf.ownedAreasEnabled && Conf.ownedAreaDenyUseage && !otherFaction.playerHasOwnershipRights(me, loc);
		
		// Cancel if we are not in our own territory
		if (!rel.isMember() && rel.confDenyUseage()) {
			if (!justCheck) {
				me.sendMessage("You can't use "+TextUtil.getMaterialName(material)+" in the territory of "+otherFaction.getTag(myFaction));
			}
			return false;
		}
		// Also cancel if player doesn't have ownership rights for this claim
		else if (rel.isMember() && ownershipFail && !Factions.hasPermOwnershipBypass(player)) {
			if (!justCheck) {
				me.sendMessage("You can't use "+TextUtil.getMaterialName(material)+" in this territory, it is owned by: "+myFaction.getOwnerListString(loc));
			}
			return false;
		}
		
		return true;
	}

	public static boolean canPlayerUseBlock(Player player, Block block, boolean justCheck) {

		if (Conf.adminBypassPlayers.contains(player.getName())) {
			return true;
		}

		Material material = block.getType();
		FLocation loc = new FLocation(block);
		Faction otherFaction = Board.getFactionAt(loc);

		// no door/chest/whatever protection in wilderness, war zones, or safe zones
		if (!otherFaction.isNormal()) {
			return true;
		}

		// We only care about some material types.
		if (otherFaction.hasPlayersOnline()){
			if ( ! Conf.territoryProtectedMaterials.contains(material)) {
				return true;
			}
		} else {
			if ( ! Conf.territoryProtectedMaterialsWhenOffline.contains(material)) {
				return true;
			}
		}
		
		FPlayer me = FPlayer.get(player);
		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelation(otherFaction);
		boolean ownershipFail = Conf.ownedAreasEnabled && Conf.ownedAreaProtectMaterials && !otherFaction.playerHasOwnershipRights(me, loc);
		
		// You may use any block unless it is another faction's territory...
		if (rel.isNeutral() || (rel.isEnemy() && Conf.territoryEnemyProtectMaterials) || (rel.isAlly() && Conf.territoryAllyProtectMaterials)) {
			if (!justCheck) {
				me.sendMessage("You can't use "+TextUtil.getMaterialName(material)+" in the territory of "+otherFaction.getTag(myFaction));
			}
			return false;
		}
		// Also cancel if player doesn't have ownership rights for this claim
		else if (rel.isMember() && ownershipFail && !Factions.hasPermOwnershipBypass(player)) {
			if (!justCheck) {
				me.sendMessage("You can't use "+TextUtil.getMaterialName(material)+" in this territory, it is owned by: "+myFaction.getOwnerListString(loc));
			}
			return false;
		}

		return true;
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		FPlayer me = FPlayer.get(event.getPlayer());
		Location home = me.getFaction().getHome();
		if (	Conf.homesEnabled && Conf.homesTeleportToOnDeath && home != null &&
				(Conf.homesRespawnFromNoPowerLossWorlds || !Conf.worldsNoPowerLoss.contains(event.getPlayer().getWorld().getName()))
			) {
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

		if ( ! this.playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false)) {
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

		if ( ! this.playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false)) {
			event.setCancelled(true);
			return;
		}
	}

	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (preventCommand(event.getMessage().toLowerCase(), event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	public static boolean preventCommand(String fullCmd, Player player) {
		if ((Conf.territoryNeutralDenyCommands.isEmpty() && Conf.territoryEnemyDenyCommands.isEmpty())) {
			return false;
		}

		FPlayer me = FPlayer.get(player);

		if (!me.isInOthersTerritory()) {
			return false;
		}

		Relation rel = me.getRelationToLocation();
		if (rel.isAtLeast(Relation.ALLY)) {
			return false;
		}

		String shortCmd = fullCmd.substring(1);	// Get rid of the slash at the beginning
		
		if (
			   rel.isNeutral()
			&& !Conf.territoryNeutralDenyCommands.isEmpty()
			&& !Conf.adminBypassPlayers.contains(me.getName())
			) {
			Iterator<String> iter = Conf.territoryNeutralDenyCommands.iterator();
			String cmdCheck;
			while (iter.hasNext()) {
				cmdCheck = iter.next();
				if (cmdCheck == null) {
					iter.remove();
					continue;
				}

				cmdCheck = cmdCheck.toLowerCase();
				if (fullCmd.startsWith(cmdCheck) || shortCmd.startsWith(cmdCheck)) {
					me.sendMessage("You can't use the command \""+fullCmd+"\" in neutral territory.");
					return true;
				}
			}
		}
		else if (
			   rel.isEnemy()
			&& !Conf.territoryEnemyDenyCommands.isEmpty()
			&& !Conf.adminBypassPlayers.contains(me.getName())
			) {
			Iterator<String> iter = Conf.territoryEnemyDenyCommands.iterator();
			String cmdCheck;
			while (iter.hasNext()) {
				cmdCheck = iter.next();
				if (cmdCheck == null) {
					iter.remove();
					continue;
				}

				cmdCheck = cmdCheck.toLowerCase();
				if (fullCmd.startsWith(cmdCheck) || shortCmd.startsWith(cmdCheck)) {
					me.sendMessage("You can't use the command \""+fullCmd+"\" in enemy territory.");
					return true;
				}
			}
		}
		return false;
	}

}
