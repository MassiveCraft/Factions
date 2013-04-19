package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class FactionsEventLeave extends FactionsEventAbstractSender
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
	
	@Override
	public void setCancelled(boolean cancelled) 
	{
		if (this.reason == PlayerLeaveReason.DISBAND || this.reason == PlayerLeaveReason.RESET)
		{
			cancelled = false;
		}
		super.setCancelled(cancelled);		
	}
	
	private final FPlayer fplayer;
	public FPlayer getFPlayer() { return this.fplayer; }
	
	private final Faction faction;
	public Faction getFaction() { return this.faction; }
	
	private final PlayerLeaveReason reason;
	public PlayerLeaveReason getReason() { return this.reason; }

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionsEventLeave(CommandSender sender, FPlayer fplayer, Faction faction, PlayerLeaveReason reason)
	{
		super(sender);
		this.fplayer = fplayer;
		this.faction = faction;
		this.reason = reason;
	}
	
	// -------------------------------------------- //
	// INTERNAL ENUM
	// -------------------------------------------- //
	
	public enum PlayerLeaveReason
	{
		KICKED, DISBAND, RESET, JOINOTHER, LEAVE
	}
	
}