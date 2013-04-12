package com.massivecraft.factions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionColl;

public class FactionCreateEvent extends Event implements Cancellable
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
	@Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
	
	// TODO: Could the fields be reorganized to achieve symmetry?
	
	private String factionTag;
	public String getFactionTag() { return this.factionTag; }
	
	private Player sender;
	
	public FPlayer getFPlayer()
	{
		return FPlayer.get(this.sender);
	}
	
	public String getFactionId()
	{
		return FactionColl.i.getNextId();
	}
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionCreateEvent(Player sender, String tag) 
	{
		this.cancelled = false;
		this.factionTag = tag;
		this.sender = sender;
	}

}