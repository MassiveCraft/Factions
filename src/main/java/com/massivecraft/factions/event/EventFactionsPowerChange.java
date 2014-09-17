package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.entity.MPlayer;

public class EventFactionsPowerChange extends EventFactionsAbstractSender
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
	
	private final MPlayer uplayer;
	public MPlayer getUPlayer() { return this.uplayer; }
	
	private final PowerChangeReason reason;
	public PowerChangeReason getReason() { return this.reason; }
	
	private double newPower;
	public double getNewPower() { return this.newPower; }
	public void setNewPower(double newPower) { this.newPower = newPower; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsPowerChange(CommandSender sender, MPlayer uplayer, PowerChangeReason reason, double newPower)
	{
		super(sender);
		this.uplayer = uplayer;
		this.reason = reason;
		this.newPower = uplayer.getLimitedPower(newPower);
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
