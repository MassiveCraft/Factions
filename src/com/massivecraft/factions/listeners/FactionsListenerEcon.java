package com.massivecraft.factions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.event.FactionsHomeChangeEvent;
import com.massivecraft.factions.integration.Econ;

public class FactionsListenerEcon implements Listener
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FactionsListenerEcon i = new FactionsListenerEcon();
	public static FactionsListenerEcon get() { return i; }
	public FactionsListenerEcon() {}
	
	// -------------------------------------------- //
	// SETUP
	// -------------------------------------------- //
	
	public void setup()
	{
		Bukkit.getPluginManager().registerEvents(this, Factions.get());
	}

	// -------------------------------------------- //
	// LISTENER
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onFactionsHomeChangeEvent(FactionsHomeChangeEvent event)
	{
		// If the faction home is being changed through a command ...
		if (!event.getReason().equals(FactionsHomeChangeEvent.REASON_COMMAND_SETHOME)) return;
		
		// ... and the sender can not afford the command ...
		if (Econ.payForAction(ConfServer.econCostSethome, event.getSender(), Factions.get().getOuterCmdFactions().cmdFactionsSethome.getDesc())) return;
		
		// ... then cancel the change.
		event.setCancelled(true);
	}
	
}
