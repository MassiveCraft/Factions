package com.massivecraft.factions.engine;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsPvpDisallowed;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;

public class EngineCanCombatHappen extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineCanCombatHappen i = new EngineCanCombatHappen();
	public static EngineCanCombatHappen get() { return i; }
	
	// -------------------------------------------- //
	// CAN COMBAT DAMAGE HAPPEN
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void canCombatDamageHappen(EntityDamageByEntityEvent event)
	{
		if (this.canCombatDamageHappen(event, true)) return;
		event.setCancelled(true);

		Entity damager = event.getDamager();
		if ( ! (damager instanceof Arrow)) return;

		damager.remove();
	}

	// mainly for flaming arrows; don't want allies or people in safe zones to be ignited even after damage event is cancelled
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void canCombatDamageHappen(EntityCombustByEntityEvent event)
	{
		EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(event.getCombuster(), event.getEntity(), EntityDamageEvent.DamageCause.FIRE, 0D);
		if (this.canCombatDamageHappen(sub, false)) return;
		event.setCancelled(true);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void canCombatDamageHappen(PotionSplashEvent event)
	{
		// If a harmful potion is splashing ...
		if (!MUtil.isHarmfulPotion(event.getPotion())) return;
		
		ProjectileSource projectileSource = event.getPotion().getShooter();
		if (! (projectileSource instanceof Entity)) return;
		
		Entity thrower = (Entity)projectileSource;

		// ... scan through affected entities to make sure they're all valid targets.
		for (LivingEntity affectedEntity : event.getAffectedEntities())
		{
			EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(thrower, affectedEntity, EntityDamageEvent.DamageCause.CUSTOM, 0D);
			if (this.canCombatDamageHappen(sub, true)) continue;
			
			// affected entity list doesn't accept modification (iter.remove() is a no-go), but this works
			event.setIntensity(affectedEntity, 0.0);
		}
	}

	// Utility method used in "canCombatDamageHappen" below.
	public static boolean falseUnlessDisallowedPvpEventCancelled(Player attacker, Player defender, EntityDamageByEntityEvent event)
	{
		EventFactionsPvpDisallowed dpe = new EventFactionsPvpDisallowed(attacker, defender, event);
		dpe.run();
		return dpe.isCancelled();
	}
	
	public boolean canCombatDamageHappen(EntityDamageByEntityEvent event, boolean notify)
	{
		boolean ret = true;
		
		// If the defender is a player ...
		Entity edefender = event.getEntity();
		if (MUtil.isntPlayer(edefender)) return true;
		Player defender = (Player)edefender;
		MPlayer mdefender = MPlayer.get(edefender);
		
		// ... and the attacker is someone else ...
		Entity eattacker = MUtil.getLiableDamager(event);
		
		// (we check null here since there may not be an attacker)
		// (lack of attacker situations can be caused by other bukkit plugins)
		if (eattacker != null && eattacker.equals(edefender)) return true;
		
		// ... gather defender PS and faction information ...
		PS defenderPs = PS.valueOf(defender.getLocation());
		Faction defenderPsFaction = BoardColl.get().getFactionAt(defenderPs);

		// ... fast evaluate if the attacker is overriding ...
		MPlayer mplayer = MPlayer.get(eattacker);
		if (mplayer != null && mplayer.isOverriding()) return true;
		
		// ... PVP flag may cause a damage block ...
		if (defenderPsFaction.getFlag(MFlag.getFlagPvp()) == false)
		{
			if (eattacker == null)
			{
				// No attacker?
				// Let's behave as if it were a player
				return falseUnlessDisallowedPvpEventCancelled(null, defender, event);
			}
			if (MUtil.isPlayer(eattacker))
			{
				ret = falseUnlessDisallowedPvpEventCancelled((Player)eattacker, defender, event);
				if (!ret && notify)
				{
					MPlayer attacker = MPlayer.get(eattacker);
					attacker.msg("<i>PVP is disabled in %s.", defenderPsFaction.describeTo(attacker));
				}
				return ret;
			}
			return defenderPsFaction.getFlag(MFlag.getFlagMonsters());
		}

		// ... and if the attacker is a player ...
		if (MUtil.isntPlayer(eattacker)) return true;
		Player attacker = (Player)eattacker;
		MPlayer uattacker = MPlayer.get(attacker);
		
		// ... does this player bypass all protection? ...
		if (MConf.get().playersWhoBypassAllProtection.contains(attacker.getName())) return true;

		// ... gather attacker PS and faction information ...
		PS attackerPs = PS.valueOf(attacker.getLocation());
		Faction attackerPsFaction = BoardColl.get().getFactionAt(attackerPs);

		// ... PVP flag may cause a damage block ...
		// (just checking the defender as above isn't enough. What about the attacker? It could be in a no-pvp area)
		// NOTE: This check is probably not that important but we could keep it anyways.
		if (attackerPsFaction.getFlag(MFlag.getFlagPvp()) == false)
		{
			ret = falseUnlessDisallowedPvpEventCancelled(attacker, defender, event);
			if (!ret && notify) uattacker.msg("<i>PVP is disabled in %s.", attackerPsFaction.describeTo(uattacker));
			return ret;
		}

		// ... are PVP rules completely ignored in this world? ...
		if (!MConf.get().worldsPvpRulesEnabled.contains(defenderPs.getWorld())) return true;

		Faction defendFaction = mdefender.getFaction();
		Faction attackFaction = uattacker.getFaction();

		if (attackFaction.isNone() && MConf.get().disablePVPForFactionlessPlayers)
		{
			ret = falseUnlessDisallowedPvpEventCancelled(attacker, defender, event);
			if (!ret && notify) uattacker.msg("<i>You can't hurt other players until you join a faction.");
			return ret;
		}
		else if (defendFaction.isNone())
		{
			if (defenderPsFaction == attackFaction && MConf.get().enablePVPAgainstFactionlessInAttackersLand)
			{
				// Allow PVP vs. Factionless in attacker's faction territory
				return true;
			}
			else if (MConf.get().disablePVPForFactionlessPlayers)
			{
				ret = falseUnlessDisallowedPvpEventCancelled(attacker, defender, event);
				if (!ret && notify) uattacker.msg("<i>You can't hurt players who are not currently in a faction.");
				return ret;
			}
			else if (attackFaction.isNone() && MConf.get().enablePVPBetweenFactionlessPlayers)
			{
				// Allow factionless vs factionless
				return true;
			}
		}

		Rel relation = defendFaction.getRelationTo(attackFaction);

		// Check the relation
		if (relation.isFriend() && defenderPsFaction.getFlag(MFlag.getFlagFriendlyire()) == false)
		{
			ret = falseUnlessDisallowedPvpEventCancelled(attacker, defender, event);
			if (!ret && notify) uattacker.msg("<i>You can't hurt %s<i>.", relation.getDescPlayerMany());
			return ret;
		}

		// You can not hurt neutrals in their own territory.
		boolean ownTerritory = mdefender.isInOwnTerritory();
		
		if (mdefender.hasFaction() && ownTerritory && relation == Rel.NEUTRAL)
		{
			ret = falseUnlessDisallowedPvpEventCancelled(attacker, defender, event);
			if (!ret && notify)
			{
				uattacker.msg("<i>You can't hurt %s<i> in their own territory unless you declare them as an enemy.", mdefender.describeTo(uattacker));
				mdefender.msg("%s<i> tried to hurt you.", uattacker.describeTo(mdefender, true));
			}
			return ret;
		}

		return true;
	}

}
