package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.entity.Faction;

public class EventFactionsOpenChange extends EventFactionsAbstractSender
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
	
	private final Faction faction;
	public Faction getFaction() { return this.faction; }
	
	private boolean newOpen;
	public boolean isNewOpen() { return this.newOpen; }
	public void setNewOpen(boolean newOpen) { this.newOpen = newOpen; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsOpenChange(CommandSender sender, Faction faction, boolean newOpen)
	{
		super(sender);
		this.faction = faction;
		this.newOpen = newOpen;
	}
	
}
