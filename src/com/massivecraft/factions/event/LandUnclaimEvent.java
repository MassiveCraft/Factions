package com.massivecraft.factions.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class LandUnclaimEvent extends Event implements Cancellable
{	
	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;
	private String factionId;
	private FLocation location;

	public LandUnclaimEvent(FLocation loc, String id)
	{
		cancelled = false;
		location = loc;
		factionId = id;
	}

	public HandlerList getHandlers() 
	{
		return handlers;
	}

	public static HandlerList getHandlerList() 
	{
		return handlers;
	}

	public Faction getFaction()
	{
		return Factions.i.get(factionId);
	}

	public FLocation getLocation()
	{
		return this.location;
	}

	@Override
	public boolean isCancelled() 
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean c) {
		cancelled = c;
	}
}
