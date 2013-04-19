package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

public class FactionsEventPowerLoss extends FactionsEventAbstractSender
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
	
	// TODO: Replace this event with a power change event? 
	
	public FactionsEventPowerLoss(CommandSender sender)
	{
		super(sender);
	}

}
