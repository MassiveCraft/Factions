package com.massivecraft.factions.event;

import com.massivecraft.factions.entity.MPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

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
	
	private final MPlayer mplayer;
	public MPlayer getMPlayer() { return this.mplayer; }
	
	private final PowerChangeReason reason;
	public PowerChangeReason getReason() { return this.reason; }
	
	private double newPower;
	public double getNewPower() { return this.newPower; }
	public void setNewPower(double newPower) { this.newPower = newPower; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsPowerChange(CommandSender sender, MPlayer mplayer, PowerChangeReason reason, double newPower)
	{
		super(sender);
		this.mplayer = mplayer;
		this.reason = reason;
		this.newPower = mplayer.getLimitedPower(newPower);
	}
	
	// -------------------------------------------- //
	// REASON ENUM
	// -------------------------------------------- //
	
	public enum PowerChangeReason
	{
		TIME,
		DEATH,
		COMMAND,
		UNDEFINED,
		;
	}

}
