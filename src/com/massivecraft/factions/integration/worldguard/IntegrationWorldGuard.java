package com.massivecraft.factions.integration.worldguard;

import com.massivecraft.massivecore.integration.IntegrationAbstract;

public class IntegrationWorldGuard extends IntegrationAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static IntegrationWorldGuard i = new IntegrationWorldGuard();
	public static IntegrationWorldGuard get() { return i; }
	private IntegrationWorldGuard() { super("WorldGuard"); }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void activate()
	{
		EngineWorldGuard.get().activate();
	}
	
	@Override
	public void deactivate()
	{
		EngineWorldGuard.get().deactivate();
	}
	
}
