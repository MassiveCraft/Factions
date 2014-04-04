package com.massivecraft.factions.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

import org.bukkit.entity.Player;


public class PowerLossEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;
	private Faction faction;
	private FPlayer fplayer;
	private String message;

	public PowerLossEvent(Faction f, FPlayer p)
	{
		cancelled = false;
		faction = f;
		fplayer = p;
	}

	@Override
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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
