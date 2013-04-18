package com.massivecraft.factions.integration.herochat;

import com.massivecraft.mcore.integration.IntegrationFeaturesAbstract;

public class HerochatFeatures extends IntegrationFeaturesAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static HerochatFeatures i = new HerochatFeatures();
	public static HerochatFeatures get() { return i; }
	private HerochatFeatures() { super("Herochat"); }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void activate()
	{
		HerochatListener.get().activate();
	}
	
	@Override
	public void deactivate()
	{
		HerochatListener.get().deactivate();
	}
	
}
