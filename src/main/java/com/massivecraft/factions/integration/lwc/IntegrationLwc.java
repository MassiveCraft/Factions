package com.massivecraft.factions.integration.lwc;

import com.massivecraft.massivecore.integration.IntegrationAbstract;

public class IntegrationLwc extends IntegrationAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static IntegrationLwc i = new IntegrationLwc();
	public static IntegrationLwc get() { return i; }
	private IntegrationLwc() { super("LWC"); }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void activate()
	{
		EngineLwc.get().activate();
	}
	
	@Override
	public void deactivate()
	{
		EngineLwc.get().deactivate();
	}
	
}
