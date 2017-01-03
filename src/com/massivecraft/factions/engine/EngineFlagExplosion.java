package com.massivecraft.factions.engine;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.ps.PS;

public class EngineFlagExplosion extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static EngineFlagExplosion i = new EngineFlagExplosion();
	public static EngineFlagExplosion get() { return i; }

	// -------------------------------------------- //
	// FLAG: EXPLOSIONS
	// -------------------------------------------- //

	protected Set<DamageCause> DAMAGE_CAUSE_EXPLOSIONS = EnumSet.of(DamageCause.BLOCK_EXPLOSION, DamageCause.ENTITY_EXPLOSION);

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockExplosion(HangingBreakEvent event)
	{
		// If a hanging entity was broken by an explosion ...
		if (event.getCause() != RemoveCause.EXPLOSION) return;
		Entity entity = event.getEntity();

		// ... and the faction there has explosions disabled ...
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(entity.getLocation()));
		if (faction.isExplosionsAllowed()) return;

		// ... then cancel.
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockExplosion(EntityDamageEvent event)
	{
		// If an explosion damages ...
		if ( ! DAMAGE_CAUSE_EXPLOSIONS.contains(event.getCause())) return;

		// ... an entity that is modified on damage ...
		if ( ! MConf.get().entityTypesEditOnDamage.contains(event.getEntityType())) return;

		// ... and the faction has explosions disabled ...
		if (BoardColl.get().getFactionAt(PS.valueOf(event.getEntity())).isExplosionsAllowed()) return;

		// ... then cancel!
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockExplosion(EntityExplodeEvent event)
	{
		// Prepare some variables:
		// Current faction
		Faction faction = null;
		// Current allowed
		Boolean allowed = true;
		// Caching to speed things up.
		Map<Faction, Boolean> faction2allowed = new HashMap<Faction, Boolean>();

		// If an explosion occurs at a location ...
		Location location = event.getLocation();

		// Check the entity. Are explosions disabled there?
		faction = BoardColl.get().getFactionAt(PS.valueOf(location));
		allowed = faction.isExplosionsAllowed();
		if (allowed == false)
		{
			event.setCancelled(true);
			return;
		}
		faction2allowed.put(faction, allowed);

		// Individually check the flag state for each block
		Iterator<Block> iter = event.blockList().iterator();
		while (iter.hasNext())
		{
			Block block = iter.next();
			faction = BoardColl.get().getFactionAt(PS.valueOf(block));
			allowed = faction2allowed.get(faction);
			if (allowed == null)
			{
				allowed = faction.isExplosionsAllowed();
				faction2allowed.put(faction, allowed);
			}

			if (allowed == false) iter.remove();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockExplosion(EntityChangeBlockEvent event)
	{
		// If a wither is changing a block ...
		Entity entity = event.getEntity();
		if (!(entity instanceof Wither)) return;

		// ... and the faction there has explosions disabled ...
		PS ps = PS.valueOf(event.getBlock());
		Faction faction = BoardColl.get().getFactionAt(ps);

		if (faction.isExplosionsAllowed()) return;

		// ... stop the block alteration.
		event.setCancelled(true);
	}

}
