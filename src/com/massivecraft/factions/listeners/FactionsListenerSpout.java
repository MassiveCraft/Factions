package com.massivecraft.factions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.SpoutFeatures;

public class FactionsListenerSpout implements Listener
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FactionsListenerSpout i = new FactionsListenerSpout();
	public static FactionsListenerSpout get() { return i; }
	public FactionsListenerSpout() {}
	
	// -------------------------------------------- //
	// SETUP
	// -------------------------------------------- //
	
	public void setup()
	{
		Bukkit.getPluginManager().registerEvents(this, Factions.get());
	}

	// TODO: These spout related methods should not be in here.
	// The spout integration needs to be moved elsewhere.
	// NOTE: Also the spout integration should not have method calls from within FactionsCore code,
	// we should instead listen to FactionsCore events. And send client updates upon non-cancelled monitor.
	
	// Setup
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void spoutSetup(PluginDisableEvent event)
	{
		SpoutFeatures.setup();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void spoutSetup(PluginEnableEvent event)
	{
		SpoutFeatures.setup();
	}
	
	// Standard
	
	public static void spoutStandard(Player player)
	{
		SpoutFeatures.updateTitleShortly(player, null);
		SpoutFeatures.updateTitleShortly(null, player);
		SpoutFeatures.updateCapeShortly(player, null);
		SpoutFeatures.updateCapeShortly(null, player);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void spoutStandard(PlayerJoinEvent event)
	{		
		spoutStandard(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void spoutStandard(PlayerTeleportEvent event)
	{
		if (event.getFrom().getWorld().equals(event.getTo().getWorld())) return;
		spoutStandard(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void spoutStandard(PlayerRespawnEvent event)
	{
		spoutStandard(event.getPlayer());
	}
	
	// Health Bar
	
	public static void spoutHealthBar(Entity entity)
	{
		if ( ! ConfServer.spoutHealthBarUnderNames) return;
		if ( ! (entity instanceof Player)) return;
		Player player = (Player)entity;
		SpoutFeatures.updateTitle(player, null);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void spoutHealthBar(EntityDamageEvent event)
	{
		spoutHealthBar(event.getEntity());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void spoutHealthBar(EntityRegainHealthEvent event)
	{
		spoutHealthBar(event.getEntity());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void spoutHealthBar(PlayerRespawnEvent event)
	{
		spoutHealthBar(event.getPlayer());
	}
	
}
