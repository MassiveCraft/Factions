package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

public class EventFactionsCreate extends EventFactionsAbstractSender
{
	// -------------------------------------------- //
	// REQUIRED EVENT CODE
	// -------------------------------------------- //
	
	private static final HandlerList handlers = new HandlerList();
	@Override public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final String factionId;
	public final String getFactionId() { return this.factionId; }
	
	private final String factionName;
	public final String getFactionName() { return this.factionName; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsCreate(CommandSender sender, String factionId, String factionName)
	{
		super(sender);
		this.factionId = factionId;
		this.factionName = factionName;
	}

}
