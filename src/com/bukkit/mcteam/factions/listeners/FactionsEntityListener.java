package com.bukkit.mcteam.factions.listeners;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

import com.bukkit.mcteam.factions.Factions;
import com.bukkit.mcteam.factions.entities.Conf;
import com.bukkit.mcteam.factions.entities.Follower;
import com.bukkit.mcteam.factions.struct.Relation;
import com.bukkit.mcteam.factions.util.Log;

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
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			return; // Some other plugin decided. Alright then.
		}
		
		Entity entity = event.getEntity();
		if ( ! (entity instanceof Player)) {
			return;
		}
		
		Entity damager = event.getDamager();
		if ( ! (damager instanceof Player)) {
			return;
		}
		
		Log.debug(((Player)entity).getName()+ " is the defender");
		Log.debug(((Player)damager).getName()+ " is the damager");
		
		Follower defender = Follower.get((Player)entity);
		Follower attacker = Follower.get((Player)damager);
		Relation relation = defender.getRelation(attacker);
		
		// Players without faction may be hurt anywhere
		if (defender.factionId == 0) {
			return;
		}
		
		// You can never hurt faction members or allies
		if (relation == Relation.MEMBER || relation == Relation.ALLY) {
			attacker.sendMessage(Conf.colorSystem+"You can't hurt "+relation.getColor()+defender.getFullName());
			event.setCancelled(true);
			return;
		}
		
		// You can not hurt neutrals in their own territory.
		if (relation == Relation.NEUTRAL && defender.isInOwnTerritory()) {
			attacker.sendMessage(Conf.colorSystem+"You can't hurt "+relation.getColor()+defender.getFullName()+" in their own territory.");
			defender.sendMessage(relation.getColor()+attacker.getFullName()+Conf.colorSystem+" tried to hurt you.");
			event.setCancelled(true);
			return;
		}
		
		// Damage will be dealt. However check if the damage should be reduced.
		if (defender.isInOwnTerritory()) {
			int damage = event.getDamage();
			int toHeal = (int)Math.round(damage * Conf.territoryShieldFactor);
			defender.heal(toHeal);
			
			// Send message
			DecimalFormat formatter = new DecimalFormat("#.#");
		    String hearts = formatter.format(toHeal / 2.0);
		    defender.sendMessage(Conf.colorSystem+"Enemy damage reduced by "+ChatColor.RED+hearts+Conf.colorSystem+" hearts.");
		}
		
	}
}
