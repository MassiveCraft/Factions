package com.massivecraft.factions.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.mcore.ps.PS;

import org.bukkit.entity.Player;

public class LandUnclaimEvent extends Event implements Cancellable
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
	
	private final PS chunk;
	public PS getChunk() { return this.chunk; }
	
	private final Faction faction;
	public Faction getFaction() { return this.faction; }
	
	private final FPlayer fplayer;
	public FPlayer getFPlayer() { return this.fplayer; }

	// TODO: These methods seem redundant? Why were they added? Can I remove them?
	public String getFactionId() { return this.faction.getId(); }
	public String getFactionTag() { return this.faction.getTag(); }
	public Player getPlayer() { return this.fplayer.getPlayer(); }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public LandUnclaimEvent(PS chunk, Faction faction, FPlayer fplayer)
	{
		this.cancelled = false;
		this.chunk = chunk.getChunk(true);
		this.faction = faction;
		this.fplayer = fplayer;
	}
	
}
