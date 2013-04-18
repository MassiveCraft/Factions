package com.massivecraft.factions.integration.herochat;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Herochat;
import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.chat.ChatFormatter;


public class HerochatListener implements Listener
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static HerochatListener i = new HerochatListener();
	public static HerochatListener get() { return i; }
	private HerochatListener() {}
	
	// -------------------------------------------- //
	// ACTIVATE & DEACTIVATE
	// -------------------------------------------- //
	
	public void activate()
	{
		Herochat.getChannelManager().addChannel(new FactionChannel());
		Herochat.getChannelManager().addChannel(new AlliesChannel());
		
		Bukkit.getPluginManager().registerEvents(this, Factions.get());
	}
	
	public void deactivate()
	{
		HandlerList.unregisterAll(this);
	}
	
	// -------------------------------------------- //
	// LISTENER
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onChannelChatEvent(ChannelChatEvent event)
	{
		// Should we even parse?
		if ( ! ConfServer.chatParseTags) return;
		
		String format = event.getFormat();
		format = format.replaceAll("&r", "§r");
		format = ChatFormatter.format(format, event.getSender().getName(), null, null); 
		event.setFormat(format);
	}
	
}
