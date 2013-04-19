package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

public class FactionsEventCreate extends FactionsEventAbstractSender
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
	
	// TODO: How do we know what universe? Should we perhaps actually create the faction?
	
	private String factionTag;
	public String getFactionTag() { return this.factionTag; }
	
	private String factionId;
	public String getFactionId() { return this.factionId; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionsEventCreate(CommandSender sender, String factionTag, String factionId)
	{
		super(sender);
		this.factionTag = factionTag;
		this.factionId = factionId;
	}

}