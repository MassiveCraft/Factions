package com.massivecraft.factions.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class FPlayerLeaveEvent extends Event implements Cancellable
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
	
	private boolean cancelled;
	@Override public boolean isCancelled() { return this.cancelled; }
	@Override
	public void setCancelled(boolean c) 
	{
		if (reason == PlayerLeaveReason.DISBAND || reason == PlayerLeaveReason.RESET)
		{
			cancelled = false;
			return;
		}
		cancelled = c;
	}
	
	private final PlayerLeaveReason reason;
	public PlayerLeaveReason getReason() { return this.reason; }
	
	private final FPlayer fplayer;
	public FPlayer getFPlayer() { return this.fplayer; }
	
	private final Faction faction;
	public Faction getFaction() { return this.faction; }

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FPlayerLeaveEvent(FPlayer fplayer, Faction faction, PlayerLeaveReason reason)
	{
		this.cancelled = false;
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