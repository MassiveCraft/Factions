package com.massivecraft.factions.integration.worldguard;

import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.Integration;

public class IntegrationWorldGuard extends Integration
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static IntegrationWorldGuard i = new IntegrationWorldGuard();
	public static IntegrationWorldGuard get() { return i; }
	private IntegrationWorldGuard()
	{
		this.setPluginName("WorldGuard");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Engine getEngine()
	{
		return EngineWorldGuard.get();
	}
	
}
