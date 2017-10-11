package com.massivecraft.factions.engine;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.util.EnumerationUtil;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wither;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		if ( ! EnumerationUtil.isEntityTypeEditOnDamage(event.getEntityType())) return;

		// ... and the faction has explosions disabled ...
		if (BoardColl.get().getFactionAt(PS.valueOf(event.getEntity())).isExplosionsAllowed()) return;

		// ... then cancel!
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockExplosion(EntityExplodeEvent event)
	{
		Location location = event.getLocation();
		Cancellable cancellable = event;
		Collection<Block> blocks = event.blockList();
		
		blockExplosion(location, cancellable, blocks);
	}
	
	// Note that this method is used by EngineV18 for the BlockExplodeEvent
	public void blockExplosion(Location location, Cancellable cancellable, Collection<Block> blocks)
	{
		// Caching to speed things up.
		Map<Faction, Boolean> faction2allowed = new HashMap<>();
		
		// Check the entity. Are explosions disabled there?
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(location));
		Boolean allowed = faction.isExplosionsAllowed();
		if (!allowed)
		{
			cancellable.setCancelled(true);
			return;
		}
		faction2allowed.put(faction, allowed);
		
		// Individually check the flag state for each block
		Iterator<Block> iterator = blocks.iterator();
		while (iterator.hasNext())
		{
			Block block = iterator.next();
			faction = BoardColl.get().getFactionAt(PS.valueOf(block));
			allowed = faction2allowed.get(faction);
			if (allowed == null)
			{
				allowed = faction.isExplosionsAllowed();
				faction2allowed.put(faction, allowed);
			}
			
			if (!allowed) iterator.remove();
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
