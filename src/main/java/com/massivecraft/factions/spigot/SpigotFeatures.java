package com.massivecraft.factions.spigot;

public class SpigotFeatures
{
	// -------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------- //
	
	// The "active" field is set from inside the EngineSpigot
	
	private static boolean active = false;
	public static boolean isActive() { return active; }
	public static void setActive(boolean active) { SpigotFeatures.active = active; }
	
	// -------------------------------------------- //
	// ACTIVATE
	// -------------------------------------------- //
	
	public static void activate()
	{
		try
		{
			EngineSpigot.get().activate();
		}
		catch (Throwable t)
		{
			// ignored
		}
	}
	
}
