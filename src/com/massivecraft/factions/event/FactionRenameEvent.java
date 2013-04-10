package com.massivecraft.factions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class FactionRenameEvent extends Event implements Cancellable
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
	
	private final FPlayer fplayer;
	public FPlayer getFPlayer() { return this.fplayer; }
	
	private final Faction faction;
	public Faction getFaction() { return this.faction; }
	
	private String tag;
	
	
	// TODO: fix these
	public Player getPlayer() { return this.fplayer.getPlayer(); }
	public String getOldFactionTag() { return this.faction.getTag(); }
	public String getFactionTag() { return this.tag; }

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionRenameEvent(FPlayer sender, String newTag) 
	{
		this.cancelled = false;
		this.fplayer = sender;
		this.faction = sender.getFaction(); // TODO: Players can only rename their own faction? A field and constructor rewrite is probably pending for this class...
		this.tag = newTag;
	}

	

	


}
