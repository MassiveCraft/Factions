package com.massivecraft.factions.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FPlayer;
import org.bukkit.entity.Player;

public class LandClaimEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;
	private FLocation location;
	private Faction faction;
	private FPlayer fplayer;

	public LandClaimEvent(FLocation loc, Faction f, FPlayer p)
	{
		cancelled = false;
		location = loc;
		faction = f;
		fplayer = p;
	}

	public HandlerList getHandlers() 
	{
		return handlers;
	}

	public static HandlerList getHandlerList() 
	{
		return handlers;
	}

	public FLocation getLocation()
	{
		return this.location;
	}

	public Faction getFaction()
	{
		return faction;
	}

	public String getFactionId()
	{
		return faction.getId();
	}

	public String getFactionTag()
	{
		return faction.getTag();
	}

	public FPlayer getFPlayer()
	{
		return fplayer;
	}

	public Player getPlayer()
	{
		return fplayer.getPlayer();
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
