package com.massivecraft.factions.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.SpoutFeatures;


public class FactionsHealthBarListener implements Listener
{
	public P p;
	public FactionsHealthBarListener(P p)
	{
		this.p = p;
	}
	
	// -------------------------------------------- //
	// HEALTH BAR
	// -------------------------------------------- //
	
	public static void possiblyUpdateHealthBar(Entity entity, Cancellable event)
	{
		if (event != null && event.isCancelled()) return;
		if ( ! Conf.spoutHealthBarUnderNames) return;
		if ( ! (entity instanceof Player)) return;
		Player player = (Player)entity;
		SpoutFeatures.updateMyAppearance(player);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void monitorEntityDamageEvent(EntityDamageEvent event)
	{
		possiblyUpdateHealthBar(event.getEntity(), event);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void monitorEntityRegainHealthEvent(EntityRegainHealthEvent event)
	{
		possiblyUpdateHealthBar(event.getEntity(), event);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void monitorPlayerRespawnEvent(PlayerRespawnEvent event)
	{
		possiblyUpdateHealthBar(event.getPlayer(), null);
	}
	
}
