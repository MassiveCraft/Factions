package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.ps.PS;

public class EventFactionsHomeChange extends EventFactionsAbstractSender
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
	
	private PS newHome;
	public PS getNewHome() { return this.newHome; }
	public void setNewHome(PS newHome) { this.newHome = newHome; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsHomeChange(CommandSender sender, Faction faction, PS newHome)
	{
		super(sender);
		this.faction = faction;
		this.newHome = newHome;
	}
	
}
