package com.massivecraft.factions.integration.lwc;

import com.massivecraft.mcore.integration.IntegrationFeaturesAbstract;

public class LwcFeatures extends IntegrationFeaturesAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static LwcFeatures i = new LwcFeatures();
	public static LwcFeatures get() { return i; }
	private LwcFeatures() { super("LWC"); }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void activate()
	{
		LwcEngine.get().activate();
	}
	
	@Override
	public void deactivate()
	{
		LwcEngine.get().deactivate();
	}
	
}
