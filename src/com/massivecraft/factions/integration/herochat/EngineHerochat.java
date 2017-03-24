package com.massivecraft.factions.integration.herochat;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Herochat;
import com.massivecraft.factions.chat.ChatFormatter;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.Engine;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class EngineHerochat extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineHerochat i = new EngineHerochat();
	public static EngineHerochat get() { return i; }
	
	// -------------------------------------------- //
	// ACTIVATE & DEACTIVATE
	// -------------------------------------------- //
	
	@Override
	public void setActiveInner(boolean active)
	{
		if ( ! active) return;
		
		Herochat.getChannelManager().addChannel(new ChannelFactionsFaction());
		Herochat.getChannelManager().addChannel(new ChannelFactionsAllies());
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
