package com.massivecraft.factions.util;

import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;

public class EntityUtil {
	public static CreatureType creatureTypeFromEntity(Entity entity) {
		if ( ! (entity instanceof Creature)) {
			return null;
		}
		
		String name = entity.getClass().getSimpleName();
		name = name.substring(5); // Remove "Craft"
		
		return CreatureType.fromName(name);
	}
}
