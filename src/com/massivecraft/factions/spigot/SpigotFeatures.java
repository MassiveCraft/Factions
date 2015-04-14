package com.massivecraft.factions.spigot;

import org.bukkit.event.player.PlayerInteractAtEntityEvent;

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
			// This line will throw if the class does not exist.
			PlayerInteractAtEntityEvent.class.getName();
			
			// But if the event class exists we activate.
			EngineSpigot.get().activate();
		}
		catch (Throwable t)
		{
			// ignored
		}
	}
	
}
