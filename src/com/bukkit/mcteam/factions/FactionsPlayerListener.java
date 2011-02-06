package com.bukkit.mcteam.factions;

import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.bukkit.mcteam.factions.entities.*;
import com.bukkit.mcteam.factions.util.*;

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
	public void onPlayerCommand(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String msg = event.getMessage();
		
		if (handleCommandOrChat(player, msg)) {
			event.setCancelled(true);
		}
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String msg = event.getMessage();
		
		// Process the command or 
		if ( ! handleCommandOrChat(player, msg) && Conf.useRelationColoredChat) {
			for (Player receiver : Factions.server.getOnlinePlayers()) {
				Follower follower = Follower.get(player);
				receiver.sendMessage("<"+follower.getFullName(Follower.get(receiver))+ChatColor.WHITE+"> "+msg);
			}
		}
		event.setCancelled(true);
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
		EM.onPlayerLogin(event.getPlayer());
		//Follower.get(event.getPlayer()).sendJoinInfo();
	}
	
	@Override
	public void onPlayerQuit(PlayerEvent event) {
		Follower follower = Follower.get(event.getPlayer()); 
		Log.debug("Saved follower on player quit: "+follower.getFullName());
		follower.save(); // We save the followers on logout in order to save their non autosaved state like power.
		EM.onPlayerLogout(event.getPlayer()); // Remove the player link.
	}
	
	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		// Did we change coord?
		Location from = event.getFrom();
		Location to = event.getTo();
		Coord coordFrom = Coord.from(from);
		Coord coordTo   = Coord.from(to);
		if (coordFrom.equals(coordTo)) {
			return;
		}
		
		// Yes we did change coord (:
		Follower me = Follower.get(event.getPlayer());
		
		if (me.isMapAutoUpdating()) {
			me.sendMessage(Board.getMap(me.getFaction(), Coord.from(me), me.getPlayer().getLocation().getYaw()), false);
		} else {
			// Did we change "host"(faction)?
			Faction factionFrom = Board.getFactionAt(coordFrom);
			Faction factionTo = Board.getFactionAt(coordTo);
			if ( factionFrom != factionTo) {
				me.sendFactionHereMessage();
			}
		}
	}
	
	
}
