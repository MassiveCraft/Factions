package com.massivecraft.factions.event;

import com.massivecraft.massivecore.event.EventMassiveCore;

public abstract class EventFactionsAbstract extends EventMassiveCore
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsAbstract()
	{
		
	}
	
	public EventFactionsAbstract(boolean isAsync)
	{
		super(isAsync);
	}
	
}
