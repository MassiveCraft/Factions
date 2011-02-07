package com.bukkit.mcteam.factions;

import java.io.File;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.bukkit.mcteam.factions.entities.*;
import com.bukkit.mcteam.factions.listeners.FactionsBlockListener;
import com.bukkit.mcteam.factions.listeners.FactionsEntityListener;
import com.bukkit.mcteam.factions.listeners.FactionsPlayerListener;
import com.bukkit.mcteam.factions.util.Log;

public class Factions extends JavaPlugin {
	public static PluginLoader pluginLoader;
	public static Server server;
	public static PluginDescriptionFile desc;
	public static File folder;
	public static File plugin;
	public static ClassLoader cLoader;
	
	private final FactionsPlayerListener playerListener = new FactionsPlayerListener(this);
	private final FactionsEntityListener entityListener = new FactionsEntityListener(this);
	private final FactionsBlockListener blockListener = new FactionsBlockListener(this);
	
	public Factions(PluginLoader pluginLoader, Server instance,	PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
		
		Factions.pluginLoader = pluginLoader;
		Factions.server = instance;
		Factions.desc = desc;
		Factions.folder = folder;
		Factions.plugin = plugin;
		Factions.cLoader = cLoader;
		
		Log.info("=== INIT START ===");
		long timeInitStart = System.currentTimeMillis();
		Log.info("You are running version: "+desc.getVersion());
		
		EM.loadAll();
		
		// Register events
		PluginManager pm = instance.getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_COMMAND, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_ENTITY, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGED, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACED, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_INTERACT, this.blockListener, Event.Priority.Normal, this);
		
		Log.info("=== INIT DONE (Took "+(System.currentTimeMillis()-timeInitStart)+"ms) ===");
		Log.threshold = Conf.logThreshold;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		
	}

}
