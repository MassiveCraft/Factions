package com.massivecraft.factions.integration.spigot;

import com.massivecraft.massivecore.Integration;
import com.massivecraft.massivecore.predicate.Predicate;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class PredicateSpigot implements Predicate<Integration>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static PredicateSpigot i = new PredicateSpigot();
	public static PredicateSpigot get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(Integration integration)
	{
		try
		{
			// This line will throw if the class does not exist.
			PlayerInteractAtEntityEvent.class.getName();
			
			return true;
		}
		catch (Throwable t)
		{
			return false;
		}
	}

}
