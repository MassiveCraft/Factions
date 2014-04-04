package com.massivecraft.factions.listeners;

import java.util.logging.Level;
import java.util.UnknownFormatConversionException;

import org.bukkit.Bukkit;
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
import com.massivecraft.factions.struct.Relation;


public class FactionsChatListener implements Listener
{
	public P p;
	public FactionsChatListener(P p)
	{
		this.p = p;
	}
	
	// this is for handling slashless command usage and faction/alliance chat, set at lowest priority so Factions gets to them first
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerEarlyChat(PlayerChatEvent event)
	{
		if (event.isCancelled()) return;
	
		Player talkingPlayer = event.getPlayer();
		String msg = event.getMessage();
		FPlayer me = FPlayers.i.get(talkingPlayer);
		ChatMode chat = me.getChatMode();

		// slashless factions commands need to be handled here if the user isn't in public chat mode
		if (chat != ChatMode.PUBLIC && p.handleCommand(talkingPlayer, msg, false, true))
		{
			if (Conf.logPlayerCommands)
				Bukkit.getLogger().log(Level.INFO, "[PLAYER_COMMAND] "+talkingPlayer.getName()+": "+msg);
			event.setCancelled(true);
			return;
		}

		// Is it a faction chat message?
		if (chat == ChatMode.FACTION)
		{
			Faction myFaction = me.getFaction();
			
			String message = String.format(Conf.factionChatFormat, me.describeTo(myFaction), msg);
			myFaction.sendMessage(message);
			
			Bukkit.getLogger().log(Level.INFO, ChatColor.stripColor("FactionChat "+myFaction.getTag()+": "+message));

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
				if(myFaction.getRelationTo(fplayer) == Relation.ALLY)
					fplayer.sendMessage(message);

				//Send to any players who are spying chat
				else if(fplayer.isSpyingChat())
					fplayer.sendMessage("[ACspy]: " + message);
			}
			
			Bukkit.getLogger().log(Level.INFO, ChatColor.stripColor("AllianceChat: "+message));
			
			event.setCancelled(true);
			return;
		}
	}

	// this is for handling insertion of the player's faction tag, set at highest priority to give other plugins a chance to modify chat first
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(PlayerChatEvent event)
	{
		if (event.isCancelled()) return;

		// Are we to insert the Faction tag into the format?
		// If we are not to insert it - we are done.
		if ( ! Conf.chatTagEnabled || Conf.chatTagHandledByAnotherPlugin) return;

		Player talkingPlayer = event.getPlayer();
		String msg = event.getMessage();
		String eventFormat = event.getFormat();
		FPlayer me = FPlayers.i.get(talkingPlayer);
		int InsertIndex = 0;
		
		if (!Conf.chatTagReplaceString.isEmpty() && eventFormat.contains(Conf.chatTagReplaceString))
		{
			// we're using the "replace" method of inserting the faction tags
			// if they stuck "[FACTION_TITLE]" in there, go ahead and do it too
			if (eventFormat.contains("[FACTION_TITLE]"))
			{
				eventFormat = eventFormat.replace("[FACTION_TITLE]", me.getTitle());
			}
			InsertIndex = eventFormat.indexOf(Conf.chatTagReplaceString);
			eventFormat = eventFormat.replace(Conf.chatTagReplaceString, "");
			Conf.chatTagPadAfter = false;
			Conf.chatTagPadBefore = false;
		}
		else if (!Conf.chatTagInsertAfterString.isEmpty() && eventFormat.contains(Conf.chatTagInsertAfterString))
		{
			// we're using the "insert after string" method
			InsertIndex = eventFormat.indexOf(Conf.chatTagInsertAfterString) + Conf.chatTagInsertAfterString.length();
		}
		else if (!Conf.chatTagInsertBeforeString.isEmpty() && eventFormat.contains(Conf.chatTagInsertBeforeString))
		{
			// we're using the "insert before string" method
			InsertIndex = eventFormat.indexOf(Conf.chatTagInsertBeforeString);
		}
		else
		{
			// we'll fall back to using the index place method
			InsertIndex = Conf.chatTagInsertIndex;
			if (InsertIndex > eventFormat.length())
				return;
		}
		
		String formatStart = eventFormat.substring(0, InsertIndex) + ((Conf.chatTagPadBefore && !me.getChatTag().isEmpty()) ? " " : "");
		String formatEnd = ((Conf.chatTagPadAfter && !me.getChatTag().isEmpty()) ? " " : "") + eventFormat.substring(InsertIndex);
		
		String nonColoredMsgFormat = formatStart + me.getChatTag().trim() + formatEnd;
		
		// Relation Colored?
		if (Conf.chatTagRelationColored)
		{
			// We must choke the standard message and send out individual messages to all players
			// Why? Because the relations will differ.
			event.setCancelled(true);
			
			for (Player listeningPlayer : event.getRecipients())
			{
				FPlayer you = FPlayers.i.get(listeningPlayer);
				String yourFormat = formatStart + me.getChatTag(you).trim() + formatEnd;
				try
				{
					listeningPlayer.sendMessage(String.format(yourFormat, talkingPlayer.getDisplayName(), msg));
				}
				catch (UnknownFormatConversionException ex)
				{
					Conf.chatTagInsertIndex = 0;
					P.p.log(Level.SEVERE, "Critical error in chat message formatting!");
					P.p.log(Level.SEVERE, "NOTE: This has been automatically fixed right now by setting chatTagInsertIndex to 0.");
					P.p.log(Level.SEVERE, "For a more proper fix, please read this regarding chat configuration: http://massivecraft.com/plugins/factions/config#Chat_configuration");
					return;
				}
			}
			
			// Write to the log... We will write the non colored message.
			String nonColoredMsg = ChatColor.stripColor(String.format(nonColoredMsgFormat, talkingPlayer.getDisplayName(), msg));
			Bukkit.getLogger().log(Level.INFO, nonColoredMsg);
		}
		else
		{
			// No relation color.
			event.setFormat(nonColoredMsgFormat);
		}
	}
}
