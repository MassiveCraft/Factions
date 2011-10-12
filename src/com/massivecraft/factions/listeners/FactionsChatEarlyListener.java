package com.massivecraft.factions.listeners;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Relation;


// this is an addtional PlayerListener for handling slashless command usage and faction chat, to be set at low priority so Factions gets to them first
public class FactionsChatEarlyListener extends PlayerListener
{
	public P p;
	public FactionsChatEarlyListener(P p)
	{
		this.p = p;
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent event)
	{
		// Is it a slashless Factions command?
		/*if ((event.getMessage().startsWith(P.p.getBaseCommand()+" ") || event.getMessage().equals(P.p.getBaseCommand())) && Conf.allowNoSlashCommand) {
			String msg = event.getMessage().trim();
			// make sure command isn't denied due to being in enemy/neutral territory
			if (!FactionsPlayerListener.preventCommand("/" + msg.toLowerCase(), event.getPlayer())) {
				List<String> parameters = TextUtil.split(msg);
				parameters.remove(0);
				CommandSender sender = event.getPlayer();
				P.p.handleCommand(sender, parameters);
			}
			event.setCancelled(true);
			return;
		}*/
		
		if (event.isCancelled()) return;
		
		if (p.handleCommand(event.getPlayer(), event.getMessage()))
		{
			event.setCancelled(true);
			return;
		}
		
		Player talkingPlayer = event.getPlayer();
		String msg = event.getMessage();
		
		// ... it was not a command. This means that it is a chat message!
		FPlayer me = FPlayers.i.get(talkingPlayer);
		
		// Is it a faction chat message?
		if (me.getChatMode() == ChatMode.FACTION)
		{
			
			String message = String.format(Conf.factionChatFormat, me.getNameAndRelevant(me), msg);
			me.getFaction().sendMessage(message);
			
			P.p.log(Level.INFO, ChatColor.stripColor("FactionChat "+me.getFaction().getTag()+": "+message));
			
			event.setCancelled(true);
			return;
			
		}
		else if (me.getChatMode() == ChatMode.ALLIANCE )
		{
			Faction myFaction = me.getFaction();
			
			String factionAndName = ChatColor.stripColor(me.getNameAndTag());
			String message = Conf.colorAlly+factionAndName+ChatColor.WHITE+" "+msg;
			
			//Send message to our own faction
			myFaction.sendMessage(message);
			for (FPlayer fplayer : FPlayers.i.getOnline())
			{
				if(myFaction.getRelationTo(fplayer) == Relation.ALLY)
				{
					//Send to all our allies
					fplayer.sendMessage(message);	
				}
			}
			
			P.p.log(Level.INFO, ChatColor.stripColor("AllianceChat "+me.getFaction().getTag()+": "+message));
			
			event.setCancelled(true);
			return;
		}
	}
}
