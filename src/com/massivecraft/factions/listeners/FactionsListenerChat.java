package com.massivecraft.factions.listeners;

import org.bukkit.Bukkit;
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
import com.massivecraft.mcore.event.MCorePlayerToRecipientChatEvent;

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
		
		if (MConf.get().chatParseTags)
		{
			Bukkit.getPluginManager().registerEvent(MCorePlayerToRecipientChatEvent.class, this, EventPriority.NORMAL, new ParseRelcolorEventExecutor(), Factions.get(), true);
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
				if (!(event instanceof AsyncPlayerChatEvent)) return;
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
				if (!(event instanceof AsyncPlayerChatEvent)) return;
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
				if (!(event instanceof MCorePlayerToRecipientChatEvent)) return;
				parseRelcolor((MCorePlayerToRecipientChatEvent)event);
			}
			catch (Throwable t)
			{
				throw new EventException(t);
			}
		}
	}

	public static void parseRelcolor(MCorePlayerToRecipientChatEvent event)
	{
		String format = event.getFormat();
		format = ChatFormatter.format(format, event.getSender(), event.getRecipient());
		event.setFormat(format);
	}
	
}
