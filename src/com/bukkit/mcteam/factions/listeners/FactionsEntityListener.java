package com.bukkit.mcteam.factions.listeners;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

import com.bukkit.mcteam.factions.Factions;
import com.bukkit.mcteam.factions.entities.Board;
import com.bukkit.mcteam.factions.entities.Conf;
import com.bukkit.mcteam.factions.entities.Coord;
import com.bukkit.mcteam.factions.entities.Follower;
import com.bukkit.mcteam.factions.struct.Relation;

public class FactionsEntityListener extends EntityListener {
	public Factions plugin;
	public FactionsEntityListener(Factions plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if ( ! (entity instanceof Player)) {
			return;
		}
	
		Player player = (Player) entity;
		Follower follower = Follower.get(player);
		follower.onDeath();
		follower.sendMessage(Conf.colorSystem+"Your power is now "+follower.getPowerRounded()+" / "+follower.getPowerMaxRounded());
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
			return; // Some other plugin decided. Alright then.
		}
		
		if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent)event;
            if ( ! this.canDamagerHurtDamagee(sub.getDamager(), sub.getEntity(), sub.getDamage())) {
    			event.setCancelled(true);
    		}
        } else if (event instanceof EntityDamageByProjectileEvent) {
        	EntityDamageByProjectileEvent sub = (EntityDamageByProjectileEvent)event;
            if ( ! this.canDamagerHurtDamagee(sub.getDamager(), sub.getEntity(), sub.getDamage())) {
    			event.setCancelled(true);
    		}
        }
	}

	@Override
	public void onEntityExplode(EntityExplodeEvent event)
	{
		if (Conf.territoryBlockCreepers && event.getEntity() instanceof LivingEntity)
		{	// creeper which might need prevention, if inside faction territory
			if (Board.get(event.getLocation().getWorld()).getFactionIdAt(Coord.from(event.getLocation())) > 0)
			{
				event.setCancelled(true);
				return;
			}
		}
	}

	public boolean canDamagerHurtDamagee(Entity damager, Entity damagee, int damage) {
		if ( ! (damager instanceof Player)) {
			return true;
		}
		
		if ( ! (damagee instanceof Player)) {
			return true;
		}
		
		Follower defender = Follower.get((Player)damagee);
		Follower attacker = Follower.get((Player)damager);
		Relation relation = defender.getRelation(attacker);
		
		//Log.debug(attacker.getName() + " attacked " + defender.getName());
		
		// Players without faction may be hurt anywhere
		if (defender.factionId == 0) {
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
			int toHeal = (int)(damage * Conf.territoryShieldFactor);
			defender.heal(toHeal);
			
			// Send message
			DecimalFormat formatter = new DecimalFormat("#.#");
		    String hearts = formatter.format(toHeal / 2.0);
		    defender.sendMessage(Conf.colorSystem+"Enemy damage reduced by "+ChatColor.RED+hearts+Conf.colorSystem+" hearts.");
		}
		
		return true;
	}
}
