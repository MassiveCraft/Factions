package com.massivecraft.factions.engine;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityBreakDoorEvent;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.ps.PS;

public class EngineFlagZombiegrief extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static EngineFlagZombiegrief i = new EngineFlagZombiegrief();
	public static EngineFlagZombiegrief get() { return i; }

	// -------------------------------------------- //
	// FLAG: ZOMBIEGRIEF
	// -------------------------------------------- //

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void denyZombieGrief(EntityBreakDoorEvent event)
	{
		// If a zombie is breaking a door ...
		Entity entity = event.getEntity();
		if (!(entity instanceof Zombie)) return;

		// ... and the faction there has zombiegrief disabled ...
		PS ps = PS.valueOf(event.getBlock());
		Faction faction = BoardColl.get().getFactionAt(ps);
		if (faction.getFlag(MFlag.getFlagZombiegrief())) return;

		// ... stop the door breakage.
		event.setCancelled(true);
	}

}
