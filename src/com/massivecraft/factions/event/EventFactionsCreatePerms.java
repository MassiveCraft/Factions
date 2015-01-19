package com.massivecraft.factions.event;

import org.bukkit.event.HandlerList;

/**
 * External plugins that add Faction perms should make sure they exist when this event is called.
 */
public class EventFactionsCreatePerms extends EventFactionsAbstract
{
	// -------------------------------------------- //
	// REQUIRED EVENT CODE
	// -------------------------------------------- //
	
	private static final HandlerList handlers = new HandlerList();
	@Override public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }

}
