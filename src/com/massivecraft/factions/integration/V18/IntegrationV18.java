package com.massivecraft.factions.integration.V18;

import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.Integration;

public class IntegrationV18 extends Integration
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static IntegrationV18 i = new IntegrationV18();
	public static IntegrationV18 get() { return i; }
	private IntegrationV18()
	{
		this.setClassNames(
			"org.bukkit.entity.ArmorStand"
		);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public Engine getEngine()
	{
		return EngineV18.get();
	}

}
