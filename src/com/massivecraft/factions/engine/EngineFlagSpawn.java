package com.massivecraft.factions.engine;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.collections.BackstringEnumSet;
import com.massivecraft.massivecore.ps.PS;

public class EngineFlagSpawn extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static EngineFlagSpawn i = new EngineFlagSpawn();
	public static EngineFlagSpawn get() { return i; }

	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //

	public static final Set<SpawnReason> NATURAL_SPAWN_REASONS = new BackstringEnumSet<>(SpawnReason.class,
		"NATURAL",
		"JOCKEY",
		"CHUNK_GEN",
		"OCELOT_BABY",
		"NETHER_PORTAL",
		"MOUNT"
	);

	// -------------------------------------------- //
	// FLAG: MONSTERS & ANIMALS
	// -------------------------------------------- //

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockMonstersAndAnimals(CreatureSpawnEvent event)
	{
		// If this is a natural spawn ..
		if ( ! NATURAL_SPAWN_REASONS.contains(event.getSpawnReason())) return;

		// ... get the spawn location ...
		Location location = event.getLocation();
		if (location == null) return;
		PS ps = PS.valueOf(location);

		// ... get the faction there ...
		Faction faction = BoardColl.get().getFactionAt(ps);
		if (faction == null) return;

		// ... get the entity type ...
		EntityType type = event.getEntityType();

		// ... and if this type can't spawn in the faction ...
		if (canSpawn(faction, type)) return;

		// ... then cancel.
		event.setCancelled(true);
	}

	public static boolean canSpawn(Faction faction, EntityType type)
	{
		if (MConf.get().entityTypesMonsters.contains(type))
		{
			// Monster
			return faction.getFlag(MFlag.getFlagMonsters());
		}
		else if (MConf.get().entityTypesAnimals.contains(type))
		{
			// Animal
			return faction.getFlag(MFlag.getFlagAnimals());
		}
		else
		{
			// Other
			return true;
		}
	}

}
