package com.massivecraft.factions.listeners;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.util.TextUtil;


// this is an addtional PlayerListener for handling slashless command usage and faction chat, to be set at low priority so Factions gets to them first
public class FactionsChatEarlyListener extends PlayerListener{

	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		if ((event.getMessage().startsWith(Factions.instance.getBaseCommand()+" ") || event.getMessage().equals(Factions.instance.getBaseCommand())) && Conf.allowNoSlashCommand) {
			String msg = event.getMessage().trim();
			// make sure command isn't denied due to being in enemy/neutral territory
			if (!FactionsPlayerListener.preventCommand("/" + msg.toLowerCase(), event.getPlayer())) {
				List<String> parameters = TextUtil.split(msg);
				parameters.remove(0);
				CommandSender sender = event.getPlayer();
				Factions.instance.handleCommand(sender, parameters);
			}
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
			Logger.getLogger("Minecraft").info(ChatColor.stripColor("FactionChat "+me.getFaction().getTag()+": "+message));
			event.setCancelled(true);
			return;
		}
	}
}
