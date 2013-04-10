package com.massivecraft.factions.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class FPlayerJoinEvent extends Event implements Cancellable
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
	
	private FPlayer fplayer;
	private Faction faction;
	private PlayerJoinReason reason;
	
	private boolean cancelled = false;
	@Override public boolean isCancelled() { return this.cancelled; }
	@Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FPlayerJoinEvent(FPlayer fplayer, Faction faction, PlayerJoinReason reason)
	{
		this.fplayer = fplayer;
		this.faction = faction;
		this.reason = reason;
	}

	public FPlayer getFPlayer()
	{
		return fplayer;
	}
	public Faction getFaction()
	{
		return faction;
	}
	public PlayerJoinReason getReason()
	{
		return reason;	
	}
	
	// -------------------------------------------- //
	// INTERNAL ENUM
	// -------------------------------------------- //
	
	public enum PlayerJoinReason
	{
		CREATE, LEADER, COMMAND
	}
	
}