package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

public class EventFactionsHomeTeleport extends EventFactionsAbstractSender
{	
	// -------------------------------------------- //
	// REQUIRED EVENT CODE
	// -------------------------------------------- //
	
	private static final HandlerList handlers = new HandlerList();
	@Override public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsHomeTeleport(CommandSender sender)
	{
		super(sender);
	}
	
}
