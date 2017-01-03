package com.massivecraft.factions.integration.V19;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.engine.EngineCombat;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.MassivePlugin;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.List;

public class EngineV19 extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static EngineV19 i = new EngineV19 ();
	public static EngineV19 get() { return i; }

	@Override
	public MassivePlugin getActivePlugin()
	{
		return Factions.get();
	}

	// -------------------------------------------- //
	// LISTENER
	// -------------------------------------------- //

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void canCombatDamageHappen(AreaEffectCloudApplyEvent event)
	{
		// If a harmful potion effect cloud is present ...
		if ( ! MUtil.isHarmfulPotion(event.getEntity().getBasePotionData().getType().getEffectType())) return;
		
		ProjectileSource projectileSource = event.getEntity().getSource();
		if ( ! (projectileSource instanceof Entity)) return;
		
		Entity thrower = (Entity)projectileSource;
		
		// ... create a dummy list to avoid ConcurrentModificationException ...
		List<LivingEntity> affectedList = new ArrayList<LivingEntity>();
		
		// ... then scan through affected entities to make sure they're all valid targets ...
		for (LivingEntity affectedEntity : event.getAffectedEntities())
		{
			EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(thrower, affectedEntity, EntityDamageEvent.DamageCause.CUSTOM, 0D);
			// Notification disabled due to the iterating nature of effect clouds.
			if (EngineCombat.get().canCombatDamageHappen(sub, false)) continue;
			
			affectedList.add(affectedEntity);
		}
		
		// finally, remove valid targets from the affected list. (Unlike splash potions, area effect cloud's affected entities list is mutable.)
		event.getAffectedEntities().removeAll(affectedList);
	}
	
}
