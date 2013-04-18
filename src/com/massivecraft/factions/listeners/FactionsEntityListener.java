package com.massivecraft.factions.listeners;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wither;
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
import com.massivecraft.factions.event.PowerLossEvent;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.Rel;
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
		if ( ! (entity instanceof Player)) return;

		Player player = (Player) entity;
		FPlayer fplayer = FPlayers.i.get(player);
		Faction faction = Board.getFactionAt(new FLocation(player.getLocation()));

		PowerLossEvent powerLossEvent = new PowerLossEvent(faction,fplayer);
		// Check for no power loss conditions
		if ( ! faction.getFlag(FFlag.POWERLOSS))
		{
			powerLossEvent.setMessage("<i>You didn't lose any power since the territory you died in works that way.");
			powerLossEvent.setCancelled(true);
		}
		else if (Conf.worldsNoPowerLoss.contains(player.getWorld().getName()))
		{
			powerLossEvent.setMessage("<i>You didn't lose any power due to the world you died in.");
			powerLossEvent.setCancelled(true);
		}
		else {
			powerLossEvent.setMessage("<i>Your power is now <h>%d / %d");
		}

		// call Event
		Bukkit.getPluginManager().callEvent(powerLossEvent);

		// Call player onDeath if the event is not cancelled
		if(!powerLossEvent.isCancelled())
		{
			fplayer.onDeath();
		}
		// Send the message from the powerLossEvent
		final String msg = powerLossEvent.getMessage();
		if (msg != null && !msg.isEmpty())
		{
			fplayer.msg(msg,fplayer.getPowerRounded(),fplayer.getPowerMaxRounded());
		}
	}

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
		// TODO: Add a no damage at all flag??
		/*else if (Conf.safeZonePreventAllDamageToPlayers && isPlayerInSafeZone(event.getEntity()))
		{
			// Players can not take any damage in a Safe Zone
			event.setCancelled(true);
		}*/
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode(EntityExplodeEvent event)
	{
		if (event.isCancelled()) return;

		Set<FLocation> explosionLocs = new HashSet<FLocation>();
		for (Block block : event.blockList())
		{
			explosionLocs.add(new FLocation(block));
		}
		for (FLocation loc : explosionLocs)
		{
			Faction faction = Board.getFactionAt(loc);
			if (faction.getFlag(FFlag.EXPLOSIONS) == false || faction.hasOfflineExplosionProtection())
			{
				// faction has explosions disabled
				event.setCancelled(true);
				return;
			}
		}

		// TNT in water/lava doesn't normally destroy any surrounding blocks, which is usually desired behavior, but...
		// this optional change below provides workaround for waterwalling providing perfect protection,
		// and makes cheap (non-obsidian) TNT cannons require minor maintenance between shots
		Block center = event.getLocation().getBlock();
		if (event.getEntity() instanceof TNTPrimed && Conf.handleExploitTNTWaterlog && center.isLiquid())
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
		PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.WEAKNESS,
		PotionEffectType.WITHER
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
				event.setIntensity(target, 0.0);  // affected entity list doesn't accept modification (iter.remove() is a no-go), but this works
			sub = null;
		}
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

		if ( ! (damagee instanceof Player)) return true;

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

		if (defLocFaction.getFlag(FFlag.PVP) == false)
		{
			if (damager instanceof Player)
			{
				if (notify)
				{
					FPlayer attacker = FPlayers.i.get((Player)damager);
					attacker.msg("<i>PVP is disabled in %s.", defLocFaction.describeTo(attacker));
				}
				return false;
			}
			return defLocFaction.getFlag(FFlag.MONSTERS);
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
		if (locFaction.getFlag(FFlag.PVP) == false)
		{
			if (notify) attacker.msg("<i>PVP is disabled in %s.", locFaction.describeTo(attacker));
			return false;
		}

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

		Rel relation = defendFaction.getRelationTo(attackFaction);

		// Check the relation
		if (defender.hasFaction() && relation.isAtLeast(Conf.friendlyFireFromRel) && defLocFaction.getFlag(FFlag.FRIENDLYFIRE) == false)
		{
			if (notify) attacker.msg("<i>You can't hurt %s<i>.", relation.getDescPlayerMany());
			return false;
		}

		// You can not hurt neutrals in their own territory.
		boolean ownTerritory = defender.isInOwnTerritory();
		if (defender.hasFaction() && ownTerritory && relation == Rel.NEUTRAL)
		{
			if (notify)
			{
				attacker.msg("<i>You can't hurt %s<i> in their own territory unless you declare them as an enemy.", defender.describeTo(attacker));
				defender.msg("%s<i> tried to hurt you.", attacker.describeTo(defender, true));
			}
			return false;
		}

		// Damage will be dealt. However check if the damage should be reduced.
		if (damage > 0.0 && defender.hasFaction() && ownTerritory && Conf.territoryShieldFactor > 0)
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
		if (event.isCancelled()) return;
		if (event.getLocation() == null) return;

		FLocation floc = new FLocation(event.getLocation());
		Faction faction = Board.getFactionAt(floc);

		if (faction.getFlag(FFlag.MONSTERS)) return;
		if ( ! Conf.monsters.contains(event.getEntityType())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTarget(EntityTargetEvent event)
	{
		if (event.isCancelled()) return;

		// if there is a target
		Entity target = event.getTarget();
		if (target == null) return;

		// We are interested in blocking targeting for certain mobs:
		if ( ! Conf.monsters.contains(MiscUtil.creatureTypeFromEntity(event.getEntity()))) return;

		FLocation floc = new FLocation(target.getLocation());
		Faction faction = Board.getFactionAt(floc);

		if (faction.getFlag(FFlag.MONSTERS)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPaintingBreak(HangingBreakEvent event)
	{
		if (event.isCancelled()) return;

		if (event.getCause() == RemoveCause.EXPLOSION)
		{
			Faction faction = Board.getFactionAt(new FLocation(event.getEntity().getLocation()));
			if (faction.getFlag(FFlag.EXPLOSIONS) == false)
			{	// faction has explosions disabled
				event.setCancelled(true);
				return;
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

		if ( ! FactionsBlockListener.playerCanBuildDestroyBlock((Player)breaker, event.getEntity().getLocation().getBlock(), "remove paintings", false))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPaintingPlace(HangingPlaceEvent event)
	{
		if (event.isCancelled()) return;

		if ( ! FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation().getBlock(), "place paintings", false) )
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityChangeBlock(EntityChangeBlockEvent event)
	{
		if (event.isCancelled()) return;

		Entity entity = event.getEntity();

		// for now, only interested in Enderman and Wither boss tomfoolery
		if (!(entity instanceof Enderman) && !(entity instanceof Wither)) return;

		FLocation floc = new FLocation(event.getBlock());
		Faction faction = Board.getFactionAt(floc);

		if (entity instanceof Enderman)
		{
			if ( ! faction.getFlag(FFlag.ENDERGRIEF))
				event.setCancelled(true);
		}
		else if (entity instanceof Wither)
		{
			if ( ! faction.getFlag(FFlag.EXPLOSIONS))
				event.setCancelled(true);
		}
	}
}
