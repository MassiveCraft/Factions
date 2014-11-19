package com.massivecraft.factions.event;

import org.bukkit.event.HandlerList;

/**
 * External plugins that add Faction flags should make sure they exist when this event is called.
 */
public class EventFactionsCreateFlags extends EventFactionsAbstract
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
	
	public EventFactionsCreateFlags()
	{
		
	}
	
	public EventFactionsCreateFlags(boolean isAsync)
	{
		super(isAsync);
	}

}
