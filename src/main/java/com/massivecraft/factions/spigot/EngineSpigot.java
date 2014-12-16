package com.massivecraft.factions.spigot;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.engine.EngineMain;
import com.massivecraft.massivecore.EngineAbstract;


public class EngineSpigot extends EngineAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineSpigot i = new EngineSpigot();
	public static EngineSpigot get() { return i; }
	private EngineSpigot() {}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Plugin getPlugin()
	{
		return Factions.get();
	}
	
	@Override
	public void activate()
	{
		super.activate();
		SpigotFeatures.setActive(true);
	}
	
	@Override
	public void deactivate()
	{
		super.deactivate();
		SpigotFeatures.setActive(false);
	}
	
	// -------------------------------------------- //
	// LISTENER
	// -------------------------------------------- //
	
	// This is a special Spigot event that fires for Minecraft 1.8 armor stands.
	// It also fires for other entity types but for those the event is buggy.
	// It seems we can only cancel interaction with armor stands from here.
	// Thus we only handle armor stands from here and handle everything else in EngineMain.
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event)
	{
		// Gather Info
		final Player player = event.getPlayer();
		final Entity entity = event.getRightClicked();
		final boolean verboose = true;
		
		// Only care for armor stands.
		if (entity.getType() != EntityType.ARMOR_STAND) return;
		
		// If we can't use ...
		if (EngineMain.canPlayerUseEntity(player, entity, verboose)) return;
		
		// ... block use.
		event.setCancelled(true);
	}
	
}
