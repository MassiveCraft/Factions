package com.massivecraft.factions.event;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;


public class EventFactionsRelationChange extends EventFactionsAbstractSender
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
	
	private final Faction otherFaction;
	public Faction getOtherFaction() { return this.otherFaction; }
	
	private Rel newRelation;
	public Rel getNewRelation() { return this.newRelation; }
	public void setNewRelation(Rel newRelation) { this.newRelation = newRelation; }

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsRelationChange(CommandSender sender, Faction faction, Faction otherFaction, Rel newRelation)
	{
		super(sender);
		this.faction = faction;
		this.otherFaction = otherFaction;
		this.newRelation = newRelation;
	}
	
}
