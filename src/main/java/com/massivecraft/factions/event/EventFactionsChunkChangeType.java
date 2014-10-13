package com.massivecraft.factions.event;

import com.massivecraft.factions.entity.Faction;

public enum EventFactionsChunkChangeType
{
	// -------------------------------------------- //
	// ENUM
	// -------------------------------------------- //
	
	NONE("none", "none"),
	BUY("buy", "bought"),
	SELL("sell", "sold"),
	CONQUER("conquer", "conquered"),
	PILLAGE("pillage", "pillaged"),
	
	// END OF LIST
	;
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public final String now;
	public final String past;
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	EventFactionsChunkChangeType(String now, String past)
	{
		this.now = now;
		this.past = past;
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public static EventFactionsChunkChangeType get(Faction oldFaction, Faction newFaction, Faction self)
	{
		if (newFaction == oldFaction) return NONE;
		if (oldFaction.isNone()) return BUY;
		if (newFaction.isNormal()) return CONQUER;
		if (oldFaction == self) return SELL;
		return PILLAGE;
	}
	
}
