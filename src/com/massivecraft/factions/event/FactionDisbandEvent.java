package com.massivecraft.factions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionColl;

public class FactionDisbandEvent extends Event implements Cancellable
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
	
	private String id;
	private Player sender;

	public Faction getFaction()
	{
		return FactionColl.i.get(id);
	}

	public FPlayer getFPlayer()
	{
		return FPlayerColl.i.get(sender);
	}

	public Player getPlayer()
	{
		return this.sender;
	}
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionDisbandEvent(Player sender, String factionId)
	{
		this.cancelled = false;
		this.sender = sender;
		this.id = factionId;
	}
	
	// -------------------------------------------- //
	// ASSORTED
	// -------------------------------------------- //

	

	
}
