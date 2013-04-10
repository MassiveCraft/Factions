package com.massivecraft.factions.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FPlayer;
import org.bukkit.entity.Player;

public class LandUnclaimAllEvent extends Event
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

	public LandUnclaimAllEvent(Faction faction, FPlayer fplayer)
	{
		this.faction = faction;
		this.fplayer = fplayer;
	}
	
}
