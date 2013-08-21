package com.massivecraft.factions.listeners;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.chat.ChatFormatter;
import com.massivecraft.factions.entity.MConf;

public class FactionsListenerChat implements Listener
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FactionsListenerChat i = new FactionsListenerChat();
	public static FactionsListenerChat get() { return i; }
	
	// -------------------------------------------- //
	// SETUP
	// -------------------------------------------- //
	
	public void setup()
	{
		HandlerList.unregisterAll(this);
		
		if (MConf.get().chatSetFormat)
		{
			Bukkit.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, this, MConf.get().chatSetFormatAt, new SetFormatEventExecutor(), Factions.get(), true);
		}
		
		if (MConf.get().chatParseTags)
		{
			Bukkit.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, this, MConf.get().chatParseTagsAt, new ParseTagsEventExecutor(), Factions.get(), true);
		}
		
		if (MConf.get().chatParseTags && MConf.get().chatParseRelcolor)
		{
			Bukkit.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, this, EventPriority.MONITOR, new ParseRelcolorEventExecutor(), Factions.get(), true);
		}
		
	}
	
	// -------------------------------------------- //
	// SET FORMAT
	// -------------------------------------------- //
	
	private class SetFormatEventExecutor implements EventExecutor
	{
		@Override
		public void execute(Listener listener, Event event) throws EventException
		{
			try
			{
				if (!AsyncPlayerChatEvent.class.isAssignableFrom(event.getClass())) return;
				setFormat((AsyncPlayerChatEvent)event);
			}
			catch (Throwable t)
			{
				throw new EventException(t);
			}
		}
	}
	
	public static void setFormat(AsyncPlayerChatEvent event)
	{
		event.setFormat(MConf.get().chatSetFormatTo);
	}
	
	// -------------------------------------------- //
	// PARSE TAGS
	// -------------------------------------------- //

	private class ParseTagsEventExecutor implements EventExecutor
	{
		@Override
		public void execute(Listener listener, Event event) throws EventException
		{
			try
			{
				if (!AsyncPlayerChatEvent.class.isAssignableFrom(event.getClass())) return;
				parseTags((AsyncPlayerChatEvent)event);
			}
			catch (Throwable t)
			{
				throw new EventException(t);
			}
		}
	}

	public static void parseTags(AsyncPlayerChatEvent event)
	{
		String format = event.getFormat();
		format = ChatFormatter.format(format, event.getPlayer(), null);
		event.setFormat(format);
	}
	
	// -------------------------------------------- //
	// PARSE RELCOLOR
	// -------------------------------------------- //
	
	private class ParseRelcolorEventExecutor implements EventExecutor
	{
		@Override
		public void execute(Listener listener, Event event) throws EventException
		{
			try
			{
				if (!AsyncPlayerChatEvent.class.isAssignableFrom(event.getClass())) return;
				parseRelcolor((AsyncPlayerChatEvent)event);
			}
			catch (Throwable t)
			{
				throw new EventException(t);
			}
		}
	}

	public static void parseRelcolor(AsyncPlayerChatEvent event)
	{
		// Pick the recipients!
		Set<Player> recipients = new HashSet<Player>();
		if (event.getRecipients().isEmpty())
		{
			// It's empty? Another plugin probably used this trick. Guess all.
			recipients.addAll(Arrays.asList(Bukkit.getOnlinePlayers()));
		}
		else
		{
			recipients.addAll(event.getRecipients());
		}
		// Avoid the message getting sent without canceling the event.
		event.getRecipients().clear();
		
		// Prepare variables
		final Player sender = event.getPlayer();
		
		// Send the per recipient message
		for (Player recipient : recipients)
		{
			String format = event.getFormat();
			format = ChatFormatter.format(format, sender, recipient);
			
			String message = String.format(format, sender.getDisplayName(), event.getMessage());
			recipient.sendMessage(message);
		}
	}
	
}
