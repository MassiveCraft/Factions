package com.massivecraft.factions.engine;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.chat.ChatFormatter;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.EngineAbstract;
import com.massivecraft.massivecore.event.EventMassiveCorePlayerToRecipientChat;
import com.massivecraft.massivecore.util.MUtil;

public class EngineChat extends EngineAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineChat i = new EngineChat();
	public static EngineChat get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Plugin getPlugin()
	{
		return Factions.get();
	}
	
	@Override
	public void activate()
	{
		super.activate();
		
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
			Bukkit.getPluginManager().registerEvent(EventMassiveCorePlayerToRecipientChat.class, this, EventPriority.NORMAL, new ParseRelcolorEventExecutor(), Factions.get(), true);
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
		Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;
		
		String format = event.getFormat();
		format = ChatFormatter.format(format, player, null);
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
				if (!(event instanceof EventMassiveCorePlayerToRecipientChat)) return;
				parseRelcolor((EventMassiveCorePlayerToRecipientChat)event);
			}
			catch (Throwable t)
			{
				throw new EventException(t);
			}
		}
	}

	public static void parseRelcolor(EventMassiveCorePlayerToRecipientChat event)
	{
		String format = event.getFormat();
		format = ChatFormatter.format(format, event.getSender(), event.getRecipient());
		event.setFormat(format);
	}
	
}
