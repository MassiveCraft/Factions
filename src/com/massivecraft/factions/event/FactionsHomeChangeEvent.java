package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.Faction;
import com.massivecraft.mcore.event.MCoreCancellableEvent;
import com.massivecraft.mcore.ps.PS;

public class FactionsHomeChangeEvent extends MCoreCancellableEvent
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public final static String REASON_COMMAND_SETHOME = "FACTIONS_COMMAND_SETHOME";
	public final static String REASON_VERIFY_FAILED = "FACTIONS_VERIFY_FAILED";
	public final static String REASON_UNDEFINED = "FACTIONS_UNDEFINED";
	
	// -------------------------------------------- //
	// REQUIRED EVENT CODE
	// -------------------------------------------- //
	
	private static final HandlerList handlers = new HandlerList();
	@Override public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final CommandSender sender;
	public CommandSender getSender() { return this.sender; }
	
	private final String reason;
	public String getReason() { return this.reason; }
	
	private final Faction faction;
	public Faction getFaction() { return this.faction; }
	
	private PS newHome;
	public PS getNewHome() { return this.newHome; }
	public void setNewHome(PS newHome) { this.newHome = newHome; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionsHomeChangeEvent(CommandSender sender, String reason, Faction faction, PS newHome)
	{
		this.sender = sender;
		this.reason = reason;
		this.faction = faction;
		this.newHome = newHome;
	}
	
}
