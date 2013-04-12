package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FactionCreateEvent extends Event implements Cancellable
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
 
	private boolean cancelled = false;
	@Override public boolean isCancelled() { return this.cancelled; }
	@Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
	
	private final CommandSender sender;
	public CommandSender getSender() { return this.sender; }
	
	// TODO: How do we know what universe? Should we perhaps actually create the faction?
	
	private String factionTag;
	public String getFactionTag() { return this.factionTag; }
	
	private String factionId;
	public String getFactionId() { return this.factionId; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionCreateEvent(CommandSender sender, String factionTag, String factionId)
	{
		this.sender = sender;
		this.factionTag = factionTag;
		this.factionId = factionId;
	}

}