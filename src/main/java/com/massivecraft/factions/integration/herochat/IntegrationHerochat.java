package com.massivecraft.factions.integration.herochat;

import com.massivecraft.massivecore.integration.IntegrationAbstract;

public class IntegrationHerochat extends IntegrationAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static IntegrationHerochat i = new IntegrationHerochat();
	public static IntegrationHerochat get() { return i; }
	private IntegrationHerochat() { super("Herochat"); }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void activate()
	{
		EngineHerochat.get().activate();
	}
	
	@Override
	public void deactivate()
	{
		EngineHerochat.get().deactivate();
	}
	
}
