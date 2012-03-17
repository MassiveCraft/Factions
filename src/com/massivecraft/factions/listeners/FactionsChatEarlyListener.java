package com.massivecraft.factions.listeners;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Rel;


// this is an addtional PlayerListener for handling slashless command usage and faction chat, to be set at low priority so Factions gets to them first
public class FactionsChatEarlyListener implements Listener
{
	public P p;
	public FactionsChatEarlyListener(P p)
	{
		this.p = p;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(PlayerChatEvent event)
	{
		if (event.isCancelled()) return;

		Player talkingPlayer = event.getPlayer();
		String msg = event.getMessage();

		FPlayer me = FPlayers.i.get(talkingPlayer);
		ChatMode chat = me.getChatMode();

		// slashless factions commands need to be handled here if the user isn't in public chat mode
		if (chat != ChatMode.PUBLIC && p.handleCommand(event.getPlayer(), event.getMessage()))
		{
			event.setCancelled(true);
			return;
		}
		
		// Is the chat a "shout"?
	    if (msg.startsWith("!") && me.getChatMode() != ChatMode.PUBLIC) 
	    {
	      msg = msg.substring(1);
	      
	      event.setMessage(msg);
	      return;
	    }

		// Is it a faction chat message?
		if (chat == ChatMode.FACTION)
		{
			Faction myFaction = me.getFaction();
 			
			String message = String.format(Conf.factionChatFormat, me.describeTo(myFaction), msg);
			myFaction.sendMessage(message);
			
			P.p.log(Level.INFO, ChatColor.stripColor("FactionChat "+myFaction.getTag()+": "+message));
			
			//Send to any players who are spying chat
			for (FPlayer fplayer : FPlayers.i.getOnline())
			{
				if(fplayer.isSpyingChat() && fplayer.getFaction() != myFaction)
					fplayer.sendMessage("[FCspy] "+myFaction.getTag()+": "+message);
			}
			
			event.setCancelled(true);
			return;
		}
		else if (chat == ChatMode.ALLIANCE)
		{
			Faction myFaction = me.getFaction();
			
			String message = String.format(Conf.allianceChatFormat, ChatColor.stripColor(me.getNameAndTag()), msg);
			
			//Send message to our own faction
			myFaction.sendMessage(message);

			//Send to all our allies
			for (FPlayer fplayer : FPlayers.i.getOnline())
			{
				if(myFaction.getRelationTo(fplayer) == Rel.ALLY)
					fplayer.sendMessage(message);
				
				//Send to any players who are spying chat
				else if(fplayer.isSpyingChat())
					fplayer.sendMessage("[ACspy]: " + message);
			}
			
			P.p.log(Level.INFO, ChatColor.stripColor("AllianceChat: "+message));
			
			event.setCancelled(true);
			return;
		}
	}
}
