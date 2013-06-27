package com.massivecraft.factions.integration.herochat;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Herochat;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.chat.ChatFormatter;
import com.massivecraft.factions.entity.MConf;


public class HerochatEngine implements Listener
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static HerochatEngine i = new HerochatEngine();
	public static HerochatEngine get() { return i; }
	private HerochatEngine() {}
	
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
		if ( ! MConf.get().chatParseTags) return;
		
		String format = event.getFormat();
		
		// We trigger a replace of HeroChats tag {default} here
		// This way we can replace faction tags hidden withing {default} as well.
		format = format.replace("{default}", event.getChannel().getFormatSupplier().getStandardFormat());
		
		format = ChatFormatter.format(format, event.getSender().getPlayer(), null); 
		event.setFormat(format);
	}
	
}
