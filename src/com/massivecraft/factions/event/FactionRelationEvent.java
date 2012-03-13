package com.massivecraft.factions.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.Faction;


public class FactionRelationEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	private Faction fsender;
	private Faction ftarget;
	private Rel foldrel;
	private Rel frel;

	public FactionRelationEvent(Faction sender, Faction target, Rel oldrel, Rel rel)
	{
		fsender = sender;
		ftarget = target;
		foldrel = oldrel;
		frel = rel;
	}

	public HandlerList getHandlers() 
	{
		return handlers;
	}

	public static HandlerList getHandlerList() 
	{
		return handlers;
	}

	public Rel getOldRelation() 
	{
		return foldrel;
	}

	public Rel getRelation() 
	{
		return frel;
	}

	public Faction getFaction()
	{
		return fsender;
	}

	public Faction getTargetFaction()
	{
		return ftarget;
	}
}
