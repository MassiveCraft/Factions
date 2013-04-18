package com.massivecraft.factions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.chat.ChatFormatter;
import com.massivecraft.mcore.util.SenderUtil;

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
		Bukkit.getPluginManager().registerEvents(this, Factions.get());
	}
	
	// -------------------------------------------- //
	// SET FORMAT
	// -------------------------------------------- //
	
	public static void setFormat(AsyncPlayerChatEvent event, EventPriority currentPriority)
	{
		// If we are setting the chat format ...
		if (!ConfServer.chatSetFormat) return;
		
		// ... and this is the right priority ...
		if (currentPriority != ConfServer.chatSetFormatAt) return;
		
		// ... then set the format.
		event.setFormat(ConfServer.chatSetFormatTo);
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void setFormatLowest(AsyncPlayerChatEvent event)
	{
		setFormat(event, EventPriority.LOWEST);
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void setFormatLow(AsyncPlayerChatEvent event)
	{
		setFormat(event, EventPriority.LOW);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void setFormatNormal(AsyncPlayerChatEvent event)
	{
		setFormat(event, EventPriority.NORMAL);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void setFormatHigh(AsyncPlayerChatEvent event)
	{
		setFormat(event, EventPriority.HIGH);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void setFormatHighest(AsyncPlayerChatEvent event)
	{
		setFormat(event, EventPriority.HIGHEST);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void setFormatMonitor(AsyncPlayerChatEvent event)
	{
		setFormat(event, EventPriority.MONITOR);
	}
	
	// -------------------------------------------- //
	// PARSE TAGS
	// -------------------------------------------- //
	
	public static void parseTags(AsyncPlayerChatEvent event, EventPriority currentPriority)
	{
		// If we are setting the chat format ...
		if (!ConfServer.chatParseTags) return;
		
		// ... and this is the right priority ...
		if (currentPriority != ConfServer.chatParseTagsAt) return;
		
		// ... then parse tags a.k.a. "format the format".
		String format = event.getFormat();
		format = ChatFormatter.format(format, SenderUtil.getSenderId(event.getPlayer()), null, null);
		event.setFormat(format);
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void parseTagsLowest(AsyncPlayerChatEvent event)
	{
		parseTags(event, EventPriority.LOWEST);
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void parseTagsLow(AsyncPlayerChatEvent event)
	{
		parseTags(event, EventPriority.LOW);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void parseTagsNormal(AsyncPlayerChatEvent event)
	{
		parseTags(event, EventPriority.NORMAL);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void parseTagsHigh(AsyncPlayerChatEvent event)
	{
		parseTags(event, EventPriority.HIGH);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void parseTagsHighest(AsyncPlayerChatEvent event)
	{
		parseTags(event, EventPriority.HIGHEST);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void parseTagsMonitor(AsyncPlayerChatEvent event)
	{
		parseTags(event, EventPriority.MONITOR);
	}
	
}
