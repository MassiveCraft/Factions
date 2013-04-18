package com.massivecraft.factions.listeners;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.massivecraft.factions.BoardColl;
import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.event.PowerLossEvent;
import com.massivecraft.mcore.ps.PS;


public class TodoFactionsEntityListener implements Listener
{

	// -------------------------------------------- //
	// POWER LOSS ON DEATH
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void powerLossOnDeath(PlayerDeathEvent event)
	{
		// If a player dies ...
		Player player = event.getEntity();
		FPlayer fplayer = FPlayerColl.get().get(player);
		
		// ... TODO: Sending the message through the event is not the best way of doing it.
		// TODO: We should listen to our own events and send message from there if we cancel.
		// 
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(player));

		PowerLossEvent powerLossEvent = new PowerLossEvent(faction, fplayer);
		
		// Check for no power loss conditions
		if ( ! faction.getFlag(FFlag.POWERLOSS))
		{
			powerLossEvent.setMessage("<i>You didn't lose any power since the territory you died in works that way.");
			powerLossEvent.setCancelled(true);
		}
		else if (ConfServer.worldsNoPowerLoss.contains(player.getWorld().getName()))
		{
			powerLossEvent.setMessage("<i>You didn't lose any power due to the world you died in.");
			powerLossEvent.setCancelled(true);
		}
		else
		{
			powerLossEvent.setMessage("<i>Your power is now <h>%d / %d");
		}

		// call Event
		Bukkit.getPluginManager().callEvent(powerLossEvent);

		// Call player onDeath if the event is not cancelled
		if ( ! powerLossEvent.isCancelled())
		{
			fplayer.setPower(fplayer.getPower() + ConfServer.powerPerDeath);
		}
		
		// Send the message from the powerLossEvent
		final String msg = powerLossEvent.getMessage();
		if (msg != null && !msg.isEmpty())
		{
			fplayer.msg(msg, fplayer.getPowerRounded(), fplayer.getPowerMaxRounded());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent)event;
			if ( ! this.canDamagerHurtDamagee(sub, true))
			{
				event.setCancelled(true);
			}
		}
	}

	// mainly for flaming arrows; don't want allies or people in safe zones to be ignited even after damage event is cancelled
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityCombustByEntity(EntityCombustByEntityEvent event)
	{
		EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(event.getCombuster(), event.getEntity(), EntityDamageEvent.DamageCause.FIRE, 0);
		if ( ! this.canDamagerHurtDamagee(sub, false))
		{
			event.setCancelled(true);
		}
	}

	private static final Set<PotionEffectType> badPotionEffects = new LinkedHashSet<PotionEffectType>(Arrays.asList(
		PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HARM, PotionEffectType.HUNGER,
		PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.WEAKNESS,
		PotionEffectType.WITHER
	));

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPotionSplashEvent(PotionSplashEvent event)
	{
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

		FPlayer defender = FPlayerColl.get().get(damagee);

		if (defender == null || defender.getPlayer() == null)
			return true;

		Location defenderLoc = defender.getPlayer().getLocation();

		Faction defLocFaction = BoardColl.get().getFactionAt(PS.valueOf(defenderLoc));

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
					FPlayer attacker = FPlayerColl.get().get((Player)damager);
					attacker.msg("<i>PVP is disabled in %s.", defLocFaction.describeTo(attacker));
				}
				return false;
			}
			return defLocFaction.getFlag(FFlag.MONSTERS);
		}

		if ( ! (damager instanceof Player))
			return true;

		FPlayer attacker = FPlayerColl.get().get((Player)damager);

		if (attacker == null || attacker.getPlayer() == null)
			return true;

		if (ConfServer.playersWhoBypassAllProtection.contains(attacker.getName())) return true;

		Faction locFaction = BoardColl.get().getFactionAt(PS.valueOf(damager));

		// so we know from above that the defender isn't in a safezone... what about the attacker, sneaky dog that he might be?
		if (locFaction.getFlag(FFlag.PVP) == false)
		{
			if (notify) attacker.msg("<i>PVP is disabled in %s.", locFaction.describeTo(attacker));
			return false;
		}

		if (ConfServer.worldsIgnorePvP.contains(defenderLoc.getWorld().getName()))
			return true;

		Faction defendFaction = defender.getFaction();
		Faction attackFaction = attacker.getFaction();

		if (attackFaction.isNone() && ConfServer.disablePVPForFactionlessPlayers)
		{
			if (notify) attacker.msg("<i>You can't hurt other players until you join a faction.");
			return false;
		}
		else if (defendFaction.isNone())
		{
			if (defLocFaction == attackFaction && ConfServer.enablePVPAgainstFactionlessInAttackersLand)
			{
				// Allow PVP vs. Factionless in attacker's faction territory
				return true;
			}
			else if (ConfServer.disablePVPForFactionlessPlayers)
			{
				if (notify) attacker.msg("<i>You can't hurt players who are not currently in a faction.");
				return false;
			}
		}

		Rel relation = defendFaction.getRelationTo(attackFaction);

		// Check the relation
		if (defender.hasFaction() && relation.isAtLeast(ConfServer.friendlyFireFromRel) && defLocFaction.getFlag(FFlag.FRIENDLYFIRE) == false)
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
		if (damage > 0.0 && defender.hasFaction() && ownTerritory && ConfServer.territoryShieldFactor > 0)
		{
			int newDamage = (int)Math.ceil(damage * (1D - ConfServer.territoryShieldFactor));
			sub.setDamage(newDamage);

			// Send message
			if (notify)
			{
				String perc = MessageFormat.format("{0,number,#%}", (ConfServer.territoryShieldFactor)); // TODO does this display correctly??
				defender.msg("<i>Enemy damage reduced by <rose>%s<i>.", perc);
			}
		}

		return true;
	}

	
	
}
