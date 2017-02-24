package com.massivecraft.factions.engine;

import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.ps.PS;

public class EngineFlagEndergrief extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static EngineFlagEndergrief i = new EngineFlagEndergrief();
	public static EngineFlagEndergrief get() { return i; }

	// -------------------------------------------- //
	// FLAG: ENDERGRIEF
	// -------------------------------------------- //

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockEndergrief(EntityChangeBlockEvent event)
	{
		// If an enderman is changing a block ...
		Entity entity = event.getEntity();
		if (!(entity instanceof Enderman)) return;

		// ... and the faction there has endergrief disabled ...
		PS ps = PS.valueOf(event.getBlock());
		Faction faction = BoardColl.get().getFactionAt(ps);
		if (faction.getFlag(MFlag.getFlagEndergrief())) return;

		// ... stop the block alteration.
		event.setCancelled(true);
	}

}
