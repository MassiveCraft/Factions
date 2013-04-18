package com.massivecraft.factions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import com.massivecraft.factions.Factions;

public class FactionsListenerChat implements Listener
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FactionsListenerChat i = new FactionsListenerChat();
	public static FactionsListenerChat get() { return i; }
	
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
	
	// TODO: 
	
}
