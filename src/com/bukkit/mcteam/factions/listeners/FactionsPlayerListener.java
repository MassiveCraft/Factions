package com.bukkit.mcteam.factions.listeners;

import java.util.*;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.bukkit.mcteam.factions.Commands;
import com.bukkit.mcteam.factions.Factions;
import com.bukkit.mcteam.factions.entities.*;
import com.bukkit.mcteam.factions.util.TextUtil;
import com.bukkit.mcteam.factions.util.Log;


public class FactionsPlayerListener extends PlayerListener{
	public Factions plugin;
	public FactionsPlayerListener(Factions plugin) {
		this.plugin = plugin;
	}

	/**
	 * If someone says something that starts with the factions base command
	 * we handle that command.
	 */
	@Override
	public void onPlayerCommandPreprocess(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String msg = event.getMessage();
		
		if (handleCommandOrChat(player, msg)) {
			event.setCancelled(true);
		}
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		if (event.isCancelled()) {
			return; // Some other plugin ate this...
		}
		
		Player talkingPlayer = event.getPlayer();
		String msg = event.getMessage();
		
		// Is this a faction command?...
		if ( handleCommandOrChat(talkingPlayer, msg) ) {
			// ... Yes it was! We should choke the chat message.
			event.setCancelled(true);
			return;
		}
		
		// ... it was not a command. This means that it is a chat message!
		Follower me = Follower.get(talkingPlayer);
		
		// Is it a faction chat message?
		if (me.isFactionChatting()) {
			String message = String.format(Conf.factionChatFormat, me.getNameAndRelevant(me), msg);
			me.getFaction().sendMessage(message, false);
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
			
			for (Player listeningPlayer : Factions.factions.getServer().getOnlinePlayers()) {
				Follower you = Follower.get(listeningPlayer);
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
	
	public boolean handleCommandOrChat(Player player, String msg) {
		ArrayList<String> tokens = TextUtil.split(msg.trim());
		if (Conf.aliasBase.contains(tokens.get(0))) {
			tokens.remove(0);
			Follower follower = Follower.get(player);
			Commands.base(follower, tokens);
			return true;
		}
		return false;
	}
	
	@Override
	public void onPlayerJoin(PlayerEvent event) {
		//Follower.get(event.getPlayer()).sendJoinInfo();
	}
	
	@Override
	public void onPlayerQuit(PlayerEvent event) {
		Follower follower = Follower.get(event.getPlayer()); 
		Log.debug("Saved follower on player quit: "+follower.getName());
		follower.save(); // We save the followers on logout in order to save their non autosaved state like power.
	}
	
	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		Follower me = Follower.get(event.getPlayer());
		
		// Did we change coord?
		Coord coordFrom = me.lastStoodInCoord;
		Coord coordTo = Coord.from(event.getTo());
		if (coordFrom.equals(coordTo)) {
			return;
		}
		
		// Yes we did change coord (:
		me.lastStoodInCoord = coordTo;
		Board board = Board.get(event.getPlayer().getWorld());
		
		if (me.isMapAutoUpdating()) {
			me.sendMessage(board.getMap(me.getFaction(), Coord.from(me), me.getPlayer().getLocation().getYaw()), false);
		} else {
			// Did we change "host"(faction)?
			Faction factionFrom = board.getFactionAt(coordFrom);
			Faction factionTo = board.getFactionAt(coordTo);
			if ( factionFrom != factionTo) {
				me.sendFactionHereMessage();
			}
		}
	}

    @Override
    public void onPlayerItem(PlayerItemEvent event) {
		if (event.isCancelled())
			return;

		if (!this.playerCanUseItemHere(event.getPlayer(), event.getBlockClicked(), event.getItem().getTypeId()))
		{
			event.setCancelled(true);
			return;
		}

	}

	//currently checking placement/use of: redstone, sign, flint&steel, beds (not detected properly by Bukkit yet), buckets (empty, water, lava), repeater
	private static Set<Integer> badItems = new HashSet(Arrays.asList(
		 new Integer[] {331, 323, 259, 355, 325, 326, 327, 356}
	));

	public boolean playerCanUseItemHere(Player player, Block block, int itemId) {

		if (!badItems.contains(new Integer(itemId))) {
			return true; // Item isn't one we're preventing.
		}

		Coord coord = Coord.parseCoord(block);
		Faction otherFaction = Board.get(player.getWorld()).getFactionAt(coord);

		if (otherFaction == null || otherFaction.id == 0) {
			return true; // This is not faction territory. Use whatever you like here.
		}

		Follower me = Follower.get(player);
		Faction myFaction = me.getFaction();

		// Cancel if we are not in our own territory
		if (myFaction != otherFaction) {
			me.sendMessage(Conf.colorSystem+"You can't use that in the territory of "+otherFaction.getTag(myFaction));
			return false;
		}

		return true;
	}
}
