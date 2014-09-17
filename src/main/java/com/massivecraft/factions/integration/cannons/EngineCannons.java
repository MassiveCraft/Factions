package com.massivecraft.factions.integration.cannons;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import at.pavlov.cannons.event.CannonUseEvent;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.listeners.FactionsListenerMain;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;

public class EngineCannons implements Listener
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineCannons i = new EngineCannons();
	public static EngineCannons get() { return i; }
	private EngineCannons() {}
	
	// -------------------------------------------- //
	// ACTIVATE & DEACTIVATE
	// -------------------------------------------- //
	
	public void activate()
	{
		Bukkit.getPluginManager().registerEvents(this, Factions.get());
	}
	
	public void deactivate()
	{
		HandlerList.unregisterAll(this);
	}
	
	// -------------------------------------------- //
	// LISTENER
	// -------------------------------------------- //
	
	@EventHandler
	public void cannonUseEvent(CannonUseEvent event)
	{
		MPlayer mplayer = MPlayer.get(event.getPlayer());
		
		// TODO: configurable? 
		// Protect players cannons, only let them use their own cannons 
		if (FactionsListenerMain.canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getCannon().getLocation()), false)) return;
		
		event.setCancelled(true);
		mplayer.msg(Txt.parse("<red>You can't use this cannon in this territory."));
	}
}
