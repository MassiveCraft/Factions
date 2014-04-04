package com.massivecraft.factions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class FactionRenameEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;
	private FPlayer fplayer;
	private Faction faction;
	private String tag;

	public FactionRenameEvent(FPlayer sender, String newTag) 
	{
		fplayer = sender;
		faction = sender.getFaction();
		tag = newTag;
		this.cancelled = false;
	}

	public Faction getFaction()
	{
		return(faction);
	}

	public FPlayer getFPlayer()
	{
		return(fplayer);
	}

	public Player getPlayer()
	{
		return(fplayer.getPlayer());
	}

	public String getOldFactionTag()
	{
		return(faction.getTag());
	}

	public String getFactionTag()
	{
		return(tag);
	}

	public HandlerList getHandlers() 
	{
		return handlers;
	}

	public static HandlerList getHandlerList() 
	{
		return handlers;
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
