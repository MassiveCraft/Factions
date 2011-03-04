package com.bukkit.mcteam.factions;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.bukkit.mcteam.factions.entities.*;
import com.bukkit.mcteam.factions.listeners.FactionsBlockListener;
import com.bukkit.mcteam.factions.listeners.FactionsEntityListener;
import com.bukkit.mcteam.factions.listeners.FactionsPlayerListener;
import com.bukkit.mcteam.factions.util.Log;

public class Factions extends JavaPlugin {
	public static Factions factions;
	
	private final FactionsPlayerListener playerListener = new FactionsPlayerListener(this);
	private final FactionsEntityListener entityListener = new FactionsEntityListener(this);
	private final FactionsBlockListener blockListener = new FactionsBlockListener(this);

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnable() {
		Factions.factions = this;
		
		Log.info("=== INIT START ===");
		long timeInitStart = System.currentTimeMillis();
		Log.info("You are running version: "+this.getDescription().getVersion());
		
		EM.loadAll();
		
		// Register events
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGED, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGED, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACED, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_INTERACT, this.blockListener, Event.Priority.Normal, this);		
		
		Log.info("=== INIT DONE (Took "+(System.currentTimeMillis()-timeInitStart)+"ms) ===");
		Log.threshold = Conf.logThreshold;
	}

}
