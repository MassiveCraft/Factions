package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;

public class FactionsEventPowerChange extends FactionsEventAbstractSender
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
	
	private final FPlayer fplayer;
	public FPlayer getFPlayer() { return this.fplayer; }
	
	private final PowerChangeReason reason;
	public PowerChangeReason getReason() { return this.reason; }
	
	private double newPower;
	public double getNewPower() { return this.newPower; }
	public void setNewPower(double newPower) { this.newPower = newPower; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionsEventPowerChange(CommandSender sender, FPlayer fplayer, PowerChangeReason reason, double newPower)
	{
		super(sender);
		this.fplayer = fplayer;
		this.reason = reason;
		this.newPower = newPower;
	}
	
	// -------------------------------------------- //
	// REASON ENUM
	// -------------------------------------------- //
	
	public enum PowerChangeReason
	{
		TIME,
		DEATH,
		UNDEFINED,
		;
	}

}
