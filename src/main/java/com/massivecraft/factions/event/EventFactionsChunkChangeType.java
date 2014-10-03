package com.massivecraft.factions.event;

public enum EventFactionsChunkChangeType
{
	// -------------------------------------------- //
	// ENUM
	// -------------------------------------------- //
	
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
	
}
