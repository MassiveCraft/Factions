package com.massivecraft.factions.integration.lwc;

import com.griefcraft.lwc.LWC;
import com.massivecraft.factions.Factions;
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
		
		LWC.getInstance().getModuleLoader().registerModule(Factions.get(), new FactionsLwcModule(Factions.get()));
	}
	
	@Override
	public void deactivate()
	{
		EngineLwc.get().deactivate();
		
		LWC.getInstance().getModuleLoader().removeModules(Factions.get());
	}
	
}
