package com.bukkit.mcteam.factions.listeners;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

import com.bukkit.mcteam.factions.Board;
import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FLocation;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.struct.Relation;

public class FactionsEntityListener extends EntityListener {
	
	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if ( ! (entity instanceof Player)) {
			return;
		}
	
		Player player = (Player) entity;
		FPlayer follower = FPlayer.get(player);
		follower.onDeath();
		follower.sendMessage("Your power is now "+follower.getPowerRounded()+" / "+follower.getPowerMaxRounded());
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

	
	// TODO what happens with the creeper or fireball then?
	@Override
	public void onEntityExplode(EntityExplodeEvent event)
	{
		if ( event.isCancelled()) {
			return;
		}
		
		// Explosions may happen in the wilderness
		if (Board.getIdAt(new FLocation(event.getLocation())) == 0) {
			return;
		}
		
		if (Conf.territoryBlockCreepers && event.getEntity() instanceof Creeper) {
			// creeper which might need prevention, if inside faction territory
			event.setCancelled(true);
		} else if (Conf.territoryBlockFireballs && event.getEntity() instanceof Fireball) {
			// ghast fireball which might need prevention, if inside faction territory
			event.setCancelled(true);
		}
	}

	public boolean canDamagerHurtDamagee(EntityDamageByEntityEvent sub) {
		Entity damager = sub.getDamager();
		Entity damagee = sub.getEntity();
		int damage = sub.getDamage();
		if ( ! (damager instanceof Player)) {
			return true;
		}
		
		if ( ! (damagee instanceof Player)) {
			return true;
		}
		
		FPlayer defender = FPlayer.get((Player)damagee);
		FPlayer attacker = FPlayer.get((Player)damager);
		Relation relation = defender.getRelation(attacker);
		
		//Log.debug(attacker.getName() + " attacked " + defender.getName());
		
		// Players without faction may be hurt anywhere
		if (defender.getFaction().getId() == 0) {
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
			int newDamage = (int)(damage * Conf.territoryShieldFactor);
			sub.setDamage(newDamage);
			
			// Send message
		    String perc = MessageFormat.format("{0,number,#%}", (1.0 - Conf.territoryShieldFactor));
		    defender.sendMessage(Conf.colorSystem+"Enemy damage reduced by "+ChatColor.RED+perc+Conf.colorSystem+".");
		}
		
		return true;
	}
}
