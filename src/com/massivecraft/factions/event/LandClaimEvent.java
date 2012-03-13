package com.massivecraft.factions.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class LandClaimEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;
	private FLocation location;
	private String factionId, playerId;

	public LandClaimEvent(FLocation loc, String id, String pid)
	{
		cancelled = false;
		location = loc;
		factionId = id;
		playerId = pid;
	}

	public HandlerList getHandlers() 
	{
		return handlers;
	}

	public static HandlerList getHandlerList() 
	{
		return handlers;
	}

	public FPlayer getFPlayer()
	{
		return FPlayers.i.get(playerId);
	}

	public FLocation getLocation()
	{
		return this.location;
	}

	public Faction getFaction()
	{
		return Factions.i.get(factionId);
	}

	@Override
	public boolean isCancelled() 
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean c) 
	{
		this.cancelled = c;
	}

}
