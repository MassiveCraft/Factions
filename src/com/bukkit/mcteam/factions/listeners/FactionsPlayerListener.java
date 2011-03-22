package com.bukkit.mcteam.factions.listeners;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.bukkit.mcteam.factions.Board;
import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FLocation;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.Factions;
import com.bukkit.mcteam.factions.util.TextUtil;


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
		
		String formatStart = event.getFormat().substring(0, Conf.chatTagInsertIndex);
		String formatEnd = event.getFormat().substring(Conf.chatTagInsertIndex);
		
		String nonColoredMsgFormat = formatStart + me.getChatTag() + formatEnd;
		
		// Relation Colored?
		if (Conf.chatTagRelationColored) {
			// We must choke the standard message and send out individual messages to all players
			// Why? Because the relations will differ.
			event.setCancelled(true);
			
			for (Player listeningPlayer : Factions.instance.getServer().getOnlinePlayers()) {
				FPlayer you = FPlayer.get(listeningPlayer);
				String yourFormat = formatStart + me.getChatTag(you) + formatEnd;
				listeningPlayer.sendMessage(String.format(yourFormat, talkingPlayer.getDisplayName(), msg));
			}
			
			// Write to the log... We will write the non colored message.
			String nonColoredMsg = String.format(nonColoredMsgFormat, talkingPlayer.getDisplayName(), msg);
			Logger.getLogger("Minecraft").info(nonColoredMsg);
		} else {
			// No relation color.
			event.setFormat(nonColoredMsgFormat);
		}
	}
	
	@Override
	public void onPlayerJoin(PlayerEvent event) {
		// Make sure that all online players do have a fplayer.
		FPlayer me = FPlayer.get(event.getPlayer());
		
		// Update the lastLoginTime for this fplayer
		me.setLastLoginTime(System.currentTimeMillis());
		
		// Run the member auto kick routine. Twice to getToTheAdmins...
		FPlayer.autoLeaveOnInactivityRoutine();
		FPlayer.autoLeaveOnInactivityRoutine();
	}
	
	
	@Override
	public void onPlayerQuit(PlayerEvent event) {
		// Save all players on player quit.
		FPlayer.save();
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
    public void onPlayerItem(PlayerItemEvent event) {
		if (event.isCancelled()) {
			return;
		}
			
		if (event.getBlockClicked() == null) {
			return;  // right-clicked on air, not a block; no worries then
		}

		if ( ! this.playerCanUseItemHere(event.getPlayer(), event.getBlockClicked(), event.getItem().getTypeId())) {
			event.setCancelled(true);
			return;
		}

	}

	//currently checking placement/use of: redstone, sign, flint&steel, beds (not currently detected by Bukkit), buckets (empty, water, lava), repeater (not currently detected by Bukkit)
	private static Set<Integer> badItems = new HashSet<Integer>(Arrays.asList(
		 new Integer[] {331, 323, 259, 355, 325, 326, 327, 356}
	));

	public boolean playerCanUseItemHere(Player player, Block block, int itemId) {

		if ( ! badItems.contains(new Integer(itemId))) {
			return true; // Item isn't one we're preventing.
		}

		Faction otherFaction = Board.getFactionAt(new FLocation(block));

		if (otherFaction == null || otherFaction.getId() == 0) {
			return true; // This is not faction territory. Use whatever you like here.
		}

		FPlayer me = FPlayer.get(player);
		Faction myFaction = me.getFaction();

		// Cancel if we are not in our own territory
		if (myFaction != otherFaction) {
			me.sendMessage("You can't use that in the territory of "+otherFaction.getTag(myFaction));
			return false;
		}

		return true;
	}
}
