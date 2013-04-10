package com.massivecraft.factions.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Rel;


public class FactionRelationEvent extends Event
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
	
	// TODO: Should this one be informative only?
	// TODO: How about making it Cancellable?
	// TODO: How about making the target relation non-final?
	
	private final Faction faction;
	public Faction getFaction() { return this.faction; }
	
	private final Faction targetFaction;
	public Faction getTargetFaction() { return this.targetFaction; }
	
	private final Rel oldRel;
	public Rel getOldRel() { return this.oldRel; }
	
	private final Rel newRel;
	public Rel getNewRel() { return this.newRel; }

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionRelationEvent(Faction faction, Faction targetFaction, Rel oldRel, Rel newRel)
	{
		this.faction = faction;
		this.targetFaction = targetFaction;
		this.oldRel = oldRel;
		this.newRel = newRel;
	}
	
}
