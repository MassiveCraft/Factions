package com.massivecraft.factions.integration.herochat;

import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.Integration;

public class IntegrationHerochat extends Integration
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static IntegrationHerochat i = new IntegrationHerochat();
	public static IntegrationHerochat get() { return i; }
	private IntegrationHerochat()
	{
		this.setPluginName("Herochat");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Engine getEngine()
	{
		return EngineHerochat.get();
	}
	
}
