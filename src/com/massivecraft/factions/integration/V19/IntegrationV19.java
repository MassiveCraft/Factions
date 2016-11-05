package com.massivecraft.factions.integration.V19;

import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.Integration;

public class IntegrationV19 extends Integration
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static IntegrationV19 i = new IntegrationV19();
	public static IntegrationV19 get() { return i; }
	private IntegrationV19()
	{
		this.setClassNames(
			"org.bukkit.event.entity.AreaEffectCloudApplyEvent"
		);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public Engine getEngine()
	{
		return EngineV19.get();
	}

}
