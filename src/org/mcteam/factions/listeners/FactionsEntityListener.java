package org.mcteam.factions.listeners;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.mcteam.factions.Board;
import org.mcteam.factions.Conf;
import org.mcteam.factions.Factions;
import org.mcteam.factions.FLocation;
import org.mcteam.factions.FPlayer;
import org.mcteam.factions.Faction;
import org.mcteam.factions.struct.Relation;
import org.mcteam.factions.util.EntityUtil;


public class FactionsEntityListener extends EntityListener {
	
	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if ( ! (entity instanceof Player)) {
			return;
		}
		
		Player player = (Player) entity;
		FPlayer fplayer = FPlayer.get(player);
		Faction faction = Board.getFactionAt(new FLocation(player.getLocation()));
		if (faction.isWarZone()) {  // war zones always override worldsNoPowerLoss either way, thus this layout
			if (! Conf.warZonePowerLoss) {
				fplayer.sendMessage("You didn't lose any power since you were in a war zone.");
				return;
			}
			if (Conf.worldsNoPowerLoss.contains(player.getWorld().getName())) {
				fplayer.sendMessage("The world you are in has power loss normally disabled, but you still lost power since you were in a war zone.");
			}
		} else if (faction.isNone() && !Conf.wildernessPowerLoss) {
			fplayer.sendMessage("You didn't lose any power since you were in the wilderness.");
			return;
		} else if (Conf.worldsNoPowerLoss.contains(player.getWorld().getName())) {
			fplayer.sendMessage("You didn't lose any power due to the world you died in.");
			return;
		}
		fplayer.onDeath();
		fplayer.sendMessage("Your power is now "+fplayer.getPowerRounded()+" / "+fplayer.getPowerMaxRounded());
	}
	
	/**
	 * Who can I hurt?
	 * I can never hurt members or allies.
	 * I can always hurt enemies.
	 * I can hurt neutrals as long as they are outside their own territory.
	 */
	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if ( event.isCancelled()) {
			return;
		}
		
		if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent)event;
            if ( ! this.canDamagerHurtDamagee(sub)) {
    			event.setCancelled(true);
    		}
        } else if (event instanceof EntityDamageByProjectileEvent) {
        	EntityDamageByProjectileEvent sub = (EntityDamageByProjectileEvent)event;
            if ( ! this.canDamagerHurtDamagee(sub)) {
    			event.setCancelled(true);
    		}
        }
	}

	
	@Override
	public void onEntityExplode(EntityExplodeEvent event)
	{
		if ( event.isCancelled()) {
			return;
		}
		
		Faction faction = Board.getFactionAt(new FLocation(event.getLocation()));
		
		if (event.getEntity() instanceof Creeper && (
				(faction.isNone() && Conf.wildernessBlockCreepers) ||
				(faction.isNormal() && Conf.territoryBlockCreepers) ||
				(faction.isWarZone() && Conf.warZoneBlockCreepers) ||
				faction.isSafeZone()
				)) {
			// creeper which needs prevention
			event.setCancelled(true);
		} else if (event.getEntity() instanceof Fireball && (
				(faction.isNone() && Conf.wildernessBlockFireballs) ||
				(faction.isNormal() && Conf.territoryBlockFireballs) ||
				(faction.isWarZone() && Conf.warZoneBlockFireballs) ||
				faction.isSafeZone()
				)) {
			// ghast fireball which needs prevention
			event.setCancelled(true);
		} else if (
				(faction.isNone() && Conf.wildernessBlockTNT) ||
				(faction.isNormal() && Conf.territoryBlockTNT) ||
				(faction.isWarZone() && Conf.warZoneBlockTNT) ||
				(faction.isSafeZone() && Conf.safeZoneBlockTNT)
				) {
			// we'll assume it's TNT, which needs prevention
			event.setCancelled(true);
		}
	}
	
	public boolean canDamagerHurtDamagee(EntityDamageByEntityEvent sub) {
		Entity damager = sub.getDamager();
		Entity damagee = sub.getEntity();
		int damage = sub.getDamage();
		
		if ( ! (damagee instanceof Player)) {
			return true;
		}
		
		FPlayer defender = FPlayer.get((Player)damagee);
		
		if (defender == null || defender.getPlayer() == null) {
			return true;
		}
		
		// Players can not take attack damage in a SafeZone
		if (Board.getFactionAt(new FLocation(defender)).isSafeZone()) {
			if (damager instanceof Player) {
				FPlayer attacker = FPlayer.get((Player)damager);
				attacker.sendMessage("You can't hurt other players in a SafeZone.");
//				defender.sendMessage(attacker.getNameAndRelevant(defender)+Conf.colorSystem+" tried to hurt you.");
			}
			return false;
		}
		
		if ( ! (damager instanceof Player)) {
			return true;
		}
		
		FPlayer attacker = FPlayer.get((Player)damager);
		
		if (attacker == null || attacker.getPlayer() == null) {
			return true;
		}
		
		Faction locFaction = Board.getFactionAt(new FLocation(attacker));
		
		// so we know from above that the defender isn't in a safezone... what about the attacker, sneaky dog that he might be?
		if (locFaction.isSafeZone()) {
			attacker.sendMessage("You can't hurt other players while you are in a SafeZone.");
			return false;
		}
		else if (locFaction.isWarZone() && Conf.warZoneFriendlyFire) {
			return true;
		}
		
		Relation relation = defender.getRelation(attacker);
		
		// Players without faction may be hurt anywhere
		if (defender.getFaction().isNone()) {
			return true;
		}
		
		// You can never hurt faction members or allies
		if (relation == Relation.MEMBER || relation == Relation.ALLY) {
			attacker.sendMessage(Conf.colorSystem+"You can't hurt "+defender.getNameAndRelevant(attacker));
			return false;
		}
		
		// You can not hurt neutrals in their own territory.
		if (relation == Relation.NEUTRAL && defender.isInOwnTerritory()) {
			attacker.sendMessage(Conf.colorSystem+"You can't hurt "+relation.getColor()+defender.getNameAndRelevant(attacker)+Conf.colorSystem+" in their own territory.");
			defender.sendMessage(attacker.getNameAndRelevant(defender)+Conf.colorSystem+" tried to hurt you.");
			return false;
		}
		
		// Damage will be dealt. However check if the damage should be reduced.
		if (defender.isInOwnTerritory() && Conf.territoryShieldFactor > 0) {
			int newDamage = (int)Math.ceil(damage * (1D - Conf.territoryShieldFactor));
			sub.setDamage(newDamage);
			
			// Send message
		    String perc = MessageFormat.format("{0,number,#%}", (Conf.territoryShieldFactor)); // TODO does this display correctly??
		    defender.sendMessage("Enemy damage reduced by "+ChatColor.RED+perc+Conf.colorSystem+".");
		}
		
		return true;
	}
	
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (Conf.safeZoneNerfedCreatureTypes.contains(event.getCreatureType()) && Board.getFactionAt(new FLocation(event.getLocation())).isSafeZone()) {
			event.setCancelled(true);
		}
	}
	
	@Override
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		// if there is a target
		Entity target = event.getTarget();
		if (target == null) {
			return;
		}
		
		// We are interested in blocking targeting for certain mobs:
		if ( ! Conf.safeZoneNerfedCreatureTypes.contains(EntityUtil.creatureTypeFromEntity(event.getEntity()))) {
			return;
		}
		
		// in case the target is in a safe zone.
		if (Board.getFactionAt(new FLocation(target.getLocation())).isSafeZone()) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onPaintingBreak(PaintingBreakEvent event)
	{
		if (event.isCancelled()) {
			return;
		}
		if (! (event instanceof PaintingBreakByEntityEvent)) {
			return;
		}

		Entity breaker = ((PaintingBreakByEntityEvent)event).getRemover();
		if (! (breaker instanceof Player)) {
			return;
		}

		FLocation loc = new FLocation(event.getPainting().getLocation());

		if ( ! this.playerCanDoPaintings((Player)breaker, loc, "remove")) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onPaintingPlace(PaintingPlaceEvent event)
	{
		if (event.isCancelled()) {
			return;
		}

		if ( ! this.playerCanDoPaintings(event.getPlayer(), new FLocation(event.getBlock()), "place")) {
			event.setCancelled(true);
		}
	}

	public boolean playerCanDoPaintings(Player player, FLocation loc, String action) {

		if (Conf.adminBypassPlayers.contains(player.getName())) {
			return true;
		}

		Faction otherFaction = Board.getFactionAt(loc);

		FPlayer me = FPlayer.get(player);

		if (otherFaction.isNone()) {
			if (!Conf.wildernessDenyBuild || Factions.hasPermAdminBypass(player)) {
				return true; // This is not faction territory. Use whatever you like here.
			}
			me.sendMessage("You can't "+action+" paintings in the wilderness.");
			return false;
		}

		if (otherFaction.isSafeZone()) {
			if (Factions.hasPermManageSafeZone(player) || !Conf.safeZoneDenyBuild) {
				return true;
			}
			me.sendMessage("You can't "+action+" paintings in a safe zone.");
			return false;
		}
		else if (otherFaction.isWarZone()) {
			if (Factions.hasPermManageWarZone(player) || !Conf.warZoneDenyBuild) {
				return true;
			}
			me.sendMessage("You can't "+action+" paintings in a war zone.");
			return false;
		}

		Faction myFaction = me.getFaction();

		// Cancel if we are not in our own territory
		if (myFaction != otherFaction && Conf.territoryDenyBuild) {
			me.sendMessage("You can't "+action+" paintings in the territory of "+otherFaction.getTag(myFaction));
			return false;
		}

		return true;
	}
}
