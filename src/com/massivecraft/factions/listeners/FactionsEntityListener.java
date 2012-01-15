package com.massivecraft.factions.listeners;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EndermanPickupEvent;
import org.bukkit.event.entity.EndermanPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.MiscUtil;


public class FactionsEntityListener extends EntityListener
{
	public P p;
	public FactionsEntityListener(P p)
	{
		this.p = p;
	}

	private static ArrayList<PotentialExplosionExploit> exploitExplosions = new ArrayList<PotentialExplosionExploit>();

	@Override
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
	@Override
	public void onEntityDamage(EntityDamageEvent event)
	{
		if ( event.isCancelled()) return;
		
		if (event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent)event;
			if ( ! this.canDamagerHurtDamagee(sub))
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
	
	@Override
	public void onEntityExplode(EntityExplodeEvent event)
	{
		if ( event.isCancelled()) return;
		
		Location loc = event.getLocation();
		
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
			event.getEntity() instanceof Creeper
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
			event.getEntity() instanceof Fireball
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
		else
		{	// TNT
			if (exploitExplosions.size() > 0)
			{	// make sure this isn't a TNT explosion exploit attempt
				int locX = event.getLocation().getBlockX();
				int locZ = event.getLocation().getBlockZ();

				for (int i = exploitExplosions.size() - 1; i >= 0; i--)
				{
					PotentialExplosionExploit ex = exploitExplosions.get(i);

					// remove anything from the list older than 10 seconds (TNT takes 4 seconds to trigger; provide some leeway)
					if (ex.timeMillis + 10000 < System.currentTimeMillis())
					{
						exploitExplosions.remove(i);
						continue;
					}

					int absX = Math.abs(ex.X - locX);
					int absZ = Math.abs(ex.Z - locZ);
					if (absX < 5 && absZ < 5) 
					{	// it sure looks like an exploit attempt
						// let's tattle on him to everyone
						String msg = "NOTICE: Player \""+ex.playerName+"\" attempted to exploit a TNT bug in the territory of \""+ex.faction.getTag()+"\" at "+ex.X+","+ex.Z+" (X,Z) using "+ex.item.name();
						P.p.log(Level.WARNING, msg);
						for (FPlayer fplayer : FPlayers.i.getOnline())
						{
							fplayer.sendMessage(msg);
						}
						event.setCancelled(true);
						exploitExplosions.remove(i);
						return;
					}
				}
			}

			if
			(
				(
					faction.isNone()
					&&
					Conf.wildernessBlockTNT
					&&
					! Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName())
				)
				||
				(
					faction.isNormal()
					&&
					(
						online ? Conf.territoryBlockTNT : Conf.territoryBlockTNTWhenOffline
					)
				)
				||
				(
					faction.isWarZone()
					&&
					Conf.warZoneBlockTNT
				)
				||
				(
					faction.isSafeZone()
					&&
					Conf.safeZoneBlockTNT
				)
			)
			{
				// we'll assume it's TNT, which needs prevention
				event.setCancelled(true);
			}
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
		Entity damager = sub.getDamager();
		Entity damagee = sub.getEntity();
		int damage = sub.getDamage();
		
		if ( ! (damagee instanceof Player))
		{
			return true;
		}
		
		FPlayer defender = FPlayers.i.get((Player)damagee);
		
		if (defender == null || defender.getPlayer() == null)
		{
			return true;
		}
		
		Location defenderLoc = defender.getPlayer().getLocation();
		
		if (Conf.worldsIgnorePvP.contains(defenderLoc.getWorld().getName()))
		{
			return true;
		}
		
		Faction defLocFaction = Board.getFactionAt(new FLocation(defenderLoc));

		// for damage caused by projectiles, getDamager() returns the projectile... what we need to know is the source
		if (damager instanceof Projectile)
		{
			damager = ((Projectile)damager).getShooter();
		}

		// Players can not take attack damage in a SafeZone, or possibly peaceful territory
		if (defLocFaction.noPvPInTerritory()) {
			if (damager instanceof Player)
			{
				FPlayer attacker = FPlayers.i.get((Player)damager);
				attacker.msg("<i>You can't hurt other players in "+(defLocFaction.isSafeZone() ? "a SafeZone." : "peaceful territory."));
				return false;
			}
			return !defLocFaction.noMonstersInTerritory();
		}
		
		if ( ! (damager instanceof Player))
		{
			return true;
		}
		
		FPlayer attacker = FPlayers.i.get((Player)damager);
		
		if (attacker == null || attacker.getPlayer() == null)
		{
			return true;
		}
		
		if (attacker.hasLoginPvpDisabled())
		{
			attacker.msg("<i>You can't hurt other players for " + Conf.noPVPDamageToOthersForXSecondsAfterLogin + " seconds after logging in.");
			return false;
		}
		
		Faction locFaction = Board.getFactionAt(new FLocation(attacker));
		
		// so we know from above that the defender isn't in a safezone... what about the attacker, sneaky dog that he might be?
		if (locFaction.noPvPInTerritory())
		{
			attacker.msg("<i>You can't hurt other players while you are in "+(locFaction.isSafeZone() ? "a SafeZone." : "peaceful territory."));
			return false;
		}
		else if (locFaction.isWarZone() && Conf.warZoneFriendlyFire)
		{
			return true;
		}
		
		Faction defendFaction = defender.getFaction();
		Faction attackFaction = attacker.getFaction();
		
		if (attackFaction.isNone() && Conf.disablePVPForFactionlessPlayers)
		{
			attacker.msg("<i>You can't hurt other players until you join a faction.");
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
				attacker.msg("<i>You can't hurt players who are not currently in a faction.");
				return false;
			}
		}
		
		if (defendFaction.isPeaceful())
		{
			attacker.msg("<i>You can't hurt players who are in a peaceful faction.");
			return false;
		}
		else if (attackFaction.isPeaceful())
		{
			attacker.msg("<i>You can't hurt players while you are in a peaceful faction.");
			return false;
		}
		
		Relation relation = defendFaction.getRelationTo(attackFaction);
		
		// You can not hurt neutral factions
		if (Conf.disablePVPBetweenNeutralFactions && relation.isNeutral())
		{
			attacker.msg("<i>You can't hurt neutral factions. Declare them as an enemy.");
			return false;
		}
		
		// Players without faction may be hurt anywhere
		if (!defender.hasFaction())
		{
			return true;
		}
		
		// You can never hurt faction members or allies
		if (relation.isMember() || relation.isAlly())
		{
			attacker.msg("<i>You can't hurt %s<i>.", defender.describeTo(attacker));
			return false;
		}
		
		boolean ownTerritory = defender.isInOwnTerritory();
		
		// You can not hurt neutrals in their own territory.
		if (ownTerritory && relation.isNeutral())
		{
			attacker.msg("<i>You can't hurt %s<i> in their own territory unless you declare them as an enemy.", defender.describeTo(attacker));
			defender.msg("%s<i> tried to hurt you.", attacker.describeTo(defender, true));
			return false;
		}
		
		// Damage will be dealt. However check if the damage should be reduced.
		if (ownTerritory && Conf.territoryShieldFactor > 0)
		{
			int newDamage = (int)Math.ceil(damage * (1D - Conf.territoryShieldFactor));
			sub.setDamage(newDamage);
			
			// Send message
		    String perc = MessageFormat.format("{0,number,#%}", (Conf.territoryShieldFactor)); // TODO does this display correctly??
		    defender.msg("<i>Enemy damage reduced by <rose>%s<i>.", perc);
		}
		
		return true;
	}
	
	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		if (event.isCancelled() || event.getLocation() == null)
		{
			return;
		}
		
		if (Conf.safeZoneNerfedCreatureTypes.contains(event.getCreatureType()) && Board.getFactionAt(new FLocation(event.getLocation())).noMonstersInTerritory())
		{
			event.setCancelled(true);
		}
	}
	
	@Override
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

	@Override
	public void onPaintingBreak(PaintingBreakEvent event)
	{
		if (event.isCancelled()) return;
		
		if (! (event instanceof PaintingBreakByEntityEvent))
		{
			return;
		}

		Entity breaker = ((PaintingBreakByEntityEvent)event).getRemover();
		if (! (breaker instanceof Player))
		{
			return;
		}

		if ( ! FactionsBlockListener.playerCanBuildDestroyBlock((Player)breaker, event.getPainting().getLocation(), "remove paintings", false))
		{
			event.setCancelled(true);
		}
	}

	@Override
	public void onPaintingPlace(PaintingPlaceEvent event)
	{
		if (event.isCancelled()) return;

		if ( ! FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "place paintings", false) )
		{
			event.setCancelled(true);
		}
	}

	@Override
	public void onEndermanPickup(EndermanPickupEvent event)
	{
		if (event.isCancelled()) return;

		if (stopEndermanBlockManipulation(event.getBlock().getLocation()))
		{
			event.setCancelled(true);
		}
	}

	@Override
	public void onEndermanPlace(EndermanPlaceEvent event)
	{
		if (event.isCancelled()) return;

		if (stopEndermanBlockManipulation(event.getLocation()))
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


	/**
	 * Since the Bukkit team still don't seem to be in any hurry to fix the problem after at least half a year,
	 * we'll track potential explosion exploits ourselves and try to prevent them
	 * For reference, canceled TNT placement next to redstone power is bugged and triggers a free explosion
	 * Same thing happens for canceled redstone torch placement next to existing TNT
	 * https://bukkit.atlassian.net/browse/BUKKIT-89
	 */

	public static void trackPotentialExplosionExploit(String playerName, Faction faction, Material item, Location location)
	{
		exploitExplosions.add(new PotentialExplosionExploit(playerName, faction, item, location));
	}

	public static class PotentialExplosionExploit
	{
		public String playerName;
		public Faction faction;
		public Material item;
		public long timeMillis;
		public int X;
		public int Z;
		
		public PotentialExplosionExploit(String playerName, Faction faction, Material item, Location location)
		{
			this.playerName = playerName;
			this.faction = faction;
			this.item = item;
			this.timeMillis = System.currentTimeMillis();
			this.X = location.getBlockX();
			this.Z = location.getBlockZ();
		}
	}
}
