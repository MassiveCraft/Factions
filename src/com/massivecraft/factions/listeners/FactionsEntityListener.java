package com.massivecraft.factions.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.text.MessageFormat;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.MiscUtil;


public class FactionsEntityListener implements Listener
{
	public P p;
	public FactionsEntityListener(P p)
	{
		this.p = p;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent event)
	{
		Entity entity = event.getEntity();
		if ( ! (entity instanceof Player))
		{
			return;
		}
		
		Player player = (Player) entity;
		FPlayer fplayer = FPlayers.i.get(player);
		Faction faction = Board.getFactionAt(new FLocation(player.getLocation()));
		if (faction.isWarZone())
		{
			// war zones always override worldsNoPowerLoss either way, thus this layout
			if (! Conf.warZonePowerLoss)
			{
				fplayer.msg("<i>You didn't lose any power since you were in a war zone.");
				return;
			}
			if (Conf.worldsNoPowerLoss.contains(player.getWorld().getName()))
			{
				fplayer.msg("<b>The world you are in has power loss normally disabled, but you still lost power since you were in a war zone.");
			}
		}
		else if (faction.isNone() && !Conf.wildernessPowerLoss && !Conf.worldsNoWildernessProtection.contains(player.getWorld().getName()))
		{
			fplayer.msg("<i>You didn't lose any power since you were in the wilderness.");
			return;
		}
		else if (Conf.worldsNoPowerLoss.contains(player.getWorld().getName()))
		{
			fplayer.msg("<i>You didn't lose any power due to the world you died in.");
			return;
		}
		else if (Conf.peacefulMembersDisablePowerLoss && fplayer.hasFaction() && fplayer.getFaction().isPeaceful())
		{
			fplayer.msg("<i>You didn't lose any power since you are in a peaceful faction.");
			return;
		}
		fplayer.onDeath();
		fplayer.msg("<i>Your power is now <h>"+fplayer.getPowerRounded()+" / "+fplayer.getPowerMaxRounded());
	}
	
	/**
	 * Who can I hurt?
	 * I can never hurt members or allies.
	 * I can always hurt enemies.
	 * I can hurt neutrals as long as they are outside their own territory.
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (event.isCancelled()) return;
		
		if (event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent)event;
			if ( ! this.canDamagerHurtDamagee(sub, true))
			{
				event.setCancelled(true);
			}
		}
		else if (Conf.safeZonePreventAllDamageToPlayers && isPlayerInSafeZone(event.getEntity()))
		{
			// Players can not take any damage in a Safe Zone
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode(EntityExplodeEvent event)
	{
		if (event.isCancelled()) return;
		
		Location loc = event.getLocation();
		Entity boomer = event.getEntity();
		Faction faction = Board.getFactionAt(new FLocation(loc));

		if (faction.noExplosionsInTerritory())
		{
			// faction is peaceful and has explosions set to disabled
			event.setCancelled(true);
			return;
		}

		boolean online = faction.hasPlayersOnline();

		if
		(
			boomer instanceof Creeper
			&&
			(
				(faction.isNone() && Conf.wildernessBlockCreepers && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName()))
				||
				(faction.isNormal() && (online ? Conf.territoryBlockCreepers : Conf.territoryBlockCreepersWhenOffline))
				||
				(faction.isWarZone() && Conf.warZoneBlockCreepers)
				||
				faction.isSafeZone()
			)
		)
		{
			// creeper which needs prevention
			event.setCancelled(true);
		}
		else if
		(
			(boomer instanceof Fireball || boomer instanceof WitherSkull || boomer instanceof Wither)
			&&
			(
				(faction.isNone() && Conf.wildernessBlockFireballs && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName()))
				||
				(faction.isNormal() && (online ? Conf.territoryBlockFireballs : Conf.territoryBlockFireballsWhenOffline))
				||
				(faction.isWarZone() && Conf.warZoneBlockFireballs)
				||
				faction.isSafeZone()
			)
		)
		{
			// ghast fireball which needs prevention
			event.setCancelled(true);
		}
		else if
		(
			boomer instanceof TNTPrimed
			&&
			(
				(faction.isNone() && Conf.wildernessBlockTNT && ! Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName()))
				||
				(faction.isNormal() && ( online ? Conf.territoryBlockTNT : Conf.territoryBlockTNTWhenOffline ))
				||
				(faction.isWarZone() && Conf.warZoneBlockTNT)
				||
				(faction.isSafeZone() && Conf.safeZoneBlockTNT)
			)
		)
		{
			// TNT which needs prevention
			event.setCancelled(true);
		}
		else if (boomer instanceof TNTPrimed && Conf.handleExploitTNTWaterlog)
		{
			// TNT in water/lava doesn't normally destroy any surrounding blocks, which is usually desired behavior, but...
			// this change below provides workaround for waterwalling providing perfect protection,
			// and makes cheap (non-obsidian) TNT cannons require minor maintenance between shots
			Block center = loc.getBlock();
			if (center.isLiquid())
			{
				// a single surrounding block in all 6 directions is broken if the material is weak enough
				List<Block> targets = new ArrayList<Block>();
				targets.add(center.getRelative(0, 0, 1));
				targets.add(center.getRelative(0, 0, -1));
				targets.add(center.getRelative(0, 1, 0));
				targets.add(center.getRelative(0, -1, 0));
				targets.add(center.getRelative(1, 0, 0));
				targets.add(center.getRelative(-1, 0, 0));
				for (Block target : targets)
				{
					int id = target.getTypeId();
					// ignore air, bedrock, water, lava, obsidian, enchanting table, etc.... too bad we can't get a blast resistance value through Bukkit yet
					if (id != 0 && (id < 7 || id > 11) && id != 49 && id != 90 && id != 116 && id != 119 && id != 120 && id != 130)
						target.breakNaturally();
				}
			}
		}
	}

	// mainly for flaming arrows; don't want allies or people in safe zones to be ignited even after damage event is cancelled
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityCombustByEntity(EntityCombustByEntityEvent event)
	{
		if (event.isCancelled()) return;
		
		EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(event.getCombuster(), event.getEntity(), EntityDamageEvent.DamageCause.FIRE, 0);
		if ( ! this.canDamagerHurtDamagee(sub, false))
			event.setCancelled(true);
		sub = null;
	}

	private static final Set<PotionEffectType> badPotionEffects = new LinkedHashSet<PotionEffectType>(Arrays.asList(
		PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HARM, PotionEffectType.HUNGER,
		PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.WEAKNESS
	));

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPotionSplashEvent(PotionSplashEvent event)
	{
		if (event.isCancelled()) return;

		// see if the potion has a harmful effect
		boolean badjuju = false;
		for (PotionEffect effect : event.getPotion().getEffects())
		{
			if (badPotionEffects.contains(effect.getType()))
			{
				badjuju = true;
				break;
			}
		}
		if ( ! badjuju) return;

		Entity thrower = event.getPotion().getShooter();

		// scan through affected entities to make sure they're all valid targets
		Iterator<LivingEntity> iter = event.getAffectedEntities().iterator();
		while (iter.hasNext())
		{
			LivingEntity target = iter.next();
			EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(thrower, target, EntityDamageEvent.DamageCause.CUSTOM, 0);
			if ( ! this.canDamagerHurtDamagee(sub, true))
				event.setIntensity(target, 0.0);  // affected entity list doesn't accept modification (so no iter.remove()), but this works
			sub = null;
		}
	}

	public boolean isPlayerInSafeZone(Entity damagee)
	{
		if ( ! (damagee instanceof Player))
		{
			return false;
		}
		if (Board.getFactionAt(new FLocation(damagee.getLocation())).isSafeZone())
		{
			return true;
		}
		return false;
	}

	public boolean canDamagerHurtDamagee(EntityDamageByEntityEvent sub)
	{
		return canDamagerHurtDamagee(sub, true);
	}

	public boolean canDamagerHurtDamagee(EntityDamageByEntityEvent sub, boolean notify)
	{
		Entity damager = sub.getDamager();
		Entity damagee = sub.getEntity();
		int damage = sub.getDamage();
		
		if ( ! (damagee instanceof Player))
			return true;

		FPlayer defender = FPlayers.i.get((Player)damagee);
		
		if (defender == null || defender.getPlayer() == null)
			return true;
		
		Location defenderLoc = defender.getPlayer().getLocation();
		Faction defLocFaction = Board.getFactionAt(new FLocation(defenderLoc));

		// for damage caused by projectiles, getDamager() returns the projectile... what we need to know is the source
		if (damager instanceof Projectile)
			damager = ((Projectile)damager).getShooter();

		if (damager == damagee)  // ender pearl usage and other self-inflicted damage
			return true;

		// Players can not take attack damage in a SafeZone, or possibly peaceful territory
		if (defLocFaction.noPvPInTerritory()) {
			if (damager instanceof Player)
			{
				if (notify)
				{
					FPlayer attacker = FPlayers.i.get((Player)damager);
					attacker.msg("<i>You can't hurt other players in "+(defLocFaction.isSafeZone() ? "a SafeZone." : "peaceful territory."));
				}
				return false;
			}
			return !defLocFaction.noMonstersInTerritory();
		}
		
		if ( ! (damager instanceof Player))
			return true;
		
		FPlayer attacker = FPlayers.i.get((Player)damager);
		
		if (attacker == null || attacker.getPlayer() == null)
			return true;

		if (Conf.playersWhoBypassAllProtection.contains(attacker.getName())) return true;

		if (attacker.hasLoginPvpDisabled())
		{
			if (notify) attacker.msg("<i>You can't hurt other players for " + Conf.noPVPDamageToOthersForXSecondsAfterLogin + " seconds after logging in.");
			return false;
		}
		
		Faction locFaction = Board.getFactionAt(new FLocation(attacker));
		
		// so we know from above that the defender isn't in a safezone... what about the attacker, sneaky dog that he might be?
		if (locFaction.noPvPInTerritory())
		{
			if (notify) attacker.msg("<i>You can't hurt other players while you are in "+(locFaction.isSafeZone() ? "a SafeZone." : "peaceful territory."));
			return false;
		}

		if (locFaction.isWarZone() && Conf.warZoneFriendlyFire)
			return true;

		if (Conf.worldsIgnorePvP.contains(defenderLoc.getWorld().getName()))
			return true;

		Faction defendFaction = defender.getFaction();
		Faction attackFaction = attacker.getFaction();
		
		if (attackFaction.isNone() && Conf.disablePVPForFactionlessPlayers)
		{
			if (notify) attacker.msg("<i>You can't hurt other players until you join a faction.");
			return false;
		}
		else if (defendFaction.isNone())
		{
			if (defLocFaction == attackFaction && Conf.enablePVPAgainstFactionlessInAttackersLand)
			{
				// Allow PVP vs. Factionless in attacker's faction territory
				return true;
			}
			else if (Conf.disablePVPForFactionlessPlayers)
			{
				if (notify) attacker.msg("<i>You can't hurt players who are not currently in a faction.");
				return false;
			}
		}
		
		if (defendFaction.isPeaceful())
		{
			if (notify) attacker.msg("<i>You can't hurt players who are in a peaceful faction.");
			return false;
		}
		else if (attackFaction.isPeaceful())
		{
			if (notify) attacker.msg("<i>You can't hurt players while you are in a peaceful faction.");
			return false;
		}
		
		Relation relation = defendFaction.getRelationTo(attackFaction);
		
		// You can not hurt neutral factions
		if (Conf.disablePVPBetweenNeutralFactions && relation.isNeutral())
		{
			if (notify) attacker.msg("<i>You can't hurt neutral factions. Declare them as an enemy.");
			return false;
		}
		
		// Players without faction may be hurt anywhere
		if (!defender.hasFaction())
			return true;
		
		// You can never hurt faction members or allies
		if (relation.isMember() || relation.isAlly())
		{
			if (notify) attacker.msg("<i>You can't hurt %s<i>.", defender.describeTo(attacker));
			return false;
		}
		
		boolean ownTerritory = defender.isInOwnTerritory();
		
		// You can not hurt neutrals in their own territory.
		if (ownTerritory && relation.isNeutral())
		{
			if (notify)
			{
				attacker.msg("<i>You can't hurt %s<i> in their own territory unless you declare them as an enemy.", defender.describeTo(attacker));
				defender.msg("%s<i> tried to hurt you.", attacker.describeTo(defender, true));
			}
			return false;
		}
		
		// Damage will be dealt. However check if the damage should be reduced.
		if (damage > 0.0 && ownTerritory && Conf.territoryShieldFactor > 0)
		{
			int newDamage = (int)Math.ceil(damage * (1D - Conf.territoryShieldFactor));
			sub.setDamage(newDamage);
			
			// Send message
			if (notify)
			{
				String perc = MessageFormat.format("{0,number,#%}", (Conf.territoryShieldFactor)); // TODO does this display correctly??
				defender.msg("<i>Enemy damage reduced by <rose>%s<i>.", perc);
			}
		}
		
		return true;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		if (event.isCancelled() || event.getLocation() == null)
		{
			return;
		}
		
		if (Conf.safeZoneNerfedCreatureTypes.contains(event.getEntityType()) && Board.getFactionAt(new FLocation(event.getLocation())).noMonstersInTerritory())
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTarget(EntityTargetEvent event)
	{
		if (event.isCancelled()) return;
		
		// if there is a target
		Entity target = event.getTarget();
		if (target == null)
		{
			return;
		}
		
		// We are interested in blocking targeting for certain mobs:
		if ( ! Conf.safeZoneNerfedCreatureTypes.contains(MiscUtil.creatureTypeFromEntity(event.getEntity())))
		{
			return;
		}
		
		// in case the target is in a safe zone.
		if (Board.getFactionAt(new FLocation(target.getLocation())).noMonstersInTerritory())
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPaintingBreak(HangingBreakEvent event)
	{
		if (event.isCancelled()) return;
		if (event.getCause() == RemoveCause.EXPLOSION)
		{
			Location loc = event.getEntity().getLocation();
			Faction faction = Board.getFactionAt(new FLocation(loc));
			if (faction.noExplosionsInTerritory())
			{
				// faction is peaceful and has explosions set to disabled
				event.setCancelled(true);
				return;
			}

			boolean online = faction.hasPlayersOnline();
			
			if
			(
					(faction.isNone() && Conf.wildernessBlockCreepers && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName()))
					||
					(faction.isNormal() && (online ? Conf.territoryBlockCreepers : Conf.territoryBlockCreepersWhenOffline))
					||
					(faction.isWarZone() && Conf.warZoneBlockCreepers)
					||
					faction.isSafeZone()
			)
			{
				// creeper which needs prevention
				event.setCancelled(true);
			}
			else if
			(
					(faction.isNone() && Conf.wildernessBlockFireballs && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName()))
					||
					(faction.isNormal() && (online ? Conf.territoryBlockFireballs : Conf.territoryBlockFireballsWhenOffline))
					||
					(faction.isWarZone() && Conf.warZoneBlockFireballs)
					||
					faction.isSafeZone()
			)
			{
				// ghast fireball which needs prevention
				event.setCancelled(true);
			}
			else if
			(
					(faction.isNone() && Conf.wildernessBlockTNT && ! Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName()))
					||
					(faction.isNormal() && ( online ? Conf.territoryBlockTNT : Conf.territoryBlockTNTWhenOffline ))
					||
					(faction.isWarZone() && Conf.warZoneBlockTNT)
					||
					(faction.isSafeZone() && Conf.safeZoneBlockTNT)
			)
			{
				// TNT which needs prevention
				event.setCancelled(true);
			}
		}
		
		if (! (event instanceof HangingBreakByEntityEvent))
		{
			return;
		}

		Entity breaker = ((HangingBreakByEntityEvent)event).getRemover();
		if (! (breaker instanceof Player))
		{
			return;
		}

		if ( ! FactionsBlockListener.playerCanBuildDestroyBlock((Player)breaker, event.getEntity().getLocation(), "remove paintings", false))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPaintingPlace(HangingPlaceEvent event)
	{
		if (event.isCancelled()) return;

		if ( ! FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "place paintings", false) )
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityChangeBlock(EntityChangeBlockEvent event)
	{
		if (event.isCancelled()) return;

		// for now, only interested in Enderman tomfoolery
		if (!(event.getEntity() instanceof Enderman)) return;

		if (stopEndermanBlockManipulation(event.getBlock().getLocation()))
		{
			event.setCancelled(true);
		}
	}

	private boolean stopEndermanBlockManipulation(Location loc)
	{
		if (loc == null)
		{
			return false;
		}
		// quick check to see if all Enderman deny options are enabled; if so, no need to check location
		if
		(
			Conf.wildernessDenyEndermanBlocks
			&&
			Conf.territoryDenyEndermanBlocks
			&&
			Conf.territoryDenyEndermanBlocksWhenOffline
			&&
			Conf.safeZoneDenyEndermanBlocks
			&&
			Conf.warZoneDenyEndermanBlocks
		)
		{
			return true;
		}

		FLocation fLoc = new FLocation(loc);
		Faction claimFaction = Board.getFactionAt(fLoc);

		if (claimFaction.isNone())
		{
			return Conf.wildernessDenyEndermanBlocks;
		}
		else if (claimFaction.isNormal())
		{
			return claimFaction.hasPlayersOnline() ? Conf.territoryDenyEndermanBlocks : Conf.territoryDenyEndermanBlocksWhenOffline;
		}
		else if (claimFaction.isSafeZone())
		{
			return Conf.safeZoneDenyEndermanBlocks;
		}
		else if (claimFaction.isWarZone())
		{
			return Conf.warZoneDenyEndermanBlocks;
		}

		return false;
	}
}
