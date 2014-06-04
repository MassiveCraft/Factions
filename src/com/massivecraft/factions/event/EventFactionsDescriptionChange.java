package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.entity.Faction;

public class EventFactionsDescriptionChange extends EventFactionsAbstractSender
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
	
	private String newDescription;
	public String getNewDescription() { return this.newDescription; }
	public void setNewDescription(String newDescription) { this.newDescription = newDescription; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsDescriptionChange(CommandSender sender, Faction faction, String newDescription)
	{
		super(sender);
		this.faction = faction;
		this.newDescription = newDescription;
	}
	
}
