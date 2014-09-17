package com.massivecraft.factions.integration.cannons;

import com.massivecraft.massivecore.integration.IntegrationAbstract;

public class IntegrationCannons extends IntegrationAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static IntegrationCannons i = new IntegrationCannons();
	public static IntegrationCannons get() { return i; }
	private IntegrationCannons() { super("Cannons"); }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void activate()
	{
		EngineCannons.get().activate();
	}
	
	@Override
	public void deactivate()
	{
		EngineCannons.get().deactivate();
	}
}
