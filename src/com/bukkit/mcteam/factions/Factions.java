package com.bukkit.mcteam.factions;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.bukkit.mcteam.factions.commands.FBaseCommand;
import com.bukkit.mcteam.factions.listeners.FactionsBlockListener;
import com.bukkit.mcteam.factions.listeners.FactionsEntityListener;
import com.bukkit.mcteam.factions.listeners.FactionsPlayerListener;
import com.bukkit.mcteam.gson.Gson;
import com.bukkit.mcteam.gson.GsonBuilder;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import me.taylorkelly.help.Help;

public class Factions extends JavaPlugin {
	// -------------------------------------------- //
	// Fields
	// -------------------------------------------- //
	public static Factions instance;
	
	public final static Gson gson = new GsonBuilder()
	.setPrettyPrinting()
	.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
	.create();
	
	private final FactionsPlayerListener playerListener = new FactionsPlayerListener();
	private final FactionsEntityListener entityListener = new FactionsEntityListener();
	private final FactionsBlockListener blockListener = new FactionsBlockListener();
	
	public static PermissionHandler Permissions;
	public static Help helpPlugin;

	// Commands
	public List<FBaseCommand> commands = new ArrayList<FBaseCommand>();
	
	public Factions() {
		Factions.instance = this;
	}
	
	
	@Override
	public void onEnable() {
		// Add the commands
		/*commands.add(new VCommandBlood());
		commands.add(new VCommandInfect());
		commands.add(new VCommandLoad());
		commands.add(new VCommandSave());
		commands.add(new VCommandTime());
		commands.add(new VCommandTurn());
		commands.add(new VCommandCure());
		commands.add(new VCommandList());
		commands.add(new VCommandVersion());*/
		
		setupPermissions();
		setupHelp();
		
		log("=== INIT START ===");
		long timeInitStart = System.currentTimeMillis();
		
		FPlayer.load();
		Faction.load();
		Board.load();
		
		// Register events
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_ITEM, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGED, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGED, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACED, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_INTERACT, this.blockListener, Event.Priority.Normal, this);		
		
		log("=== INIT DONE (Took "+(System.currentTimeMillis()-timeInitStart)+"ms) ===");
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

	// -------------------------------------------- //
	// Integration with other plugins
	// -------------------------------------------- //
	
	private void setupPermissions() {
		if (Permissions != null) {
			return;
		}
		
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
		
		if (test != null) {
			Permissions = ((Permissions)test).getHandler();
			Factions.log("Found and will use plugin "+((Permissions)test).getDescription().getFullName());
		} else {
			Factions.log("Permission system not detected, defaulting to OP");
		}
	}
	
	private void setupHelp() {
		if (helpPlugin != null) {
			return;
		}
		
		Plugin test = this.getServer().getPluginManager().getPlugin("Help");
		
		if (test != null) {
			helpPlugin = ((Help) test);
			Factions.log("Found and will use plugin "+helpPlugin.getDescription().getFullName());
			
			// TODO not hardcoded:
			helpPlugin.registerCommand("f help *[page]", "Factions plugin help.", helpPlugin, true);
		}
	}

	
	// -------------------------------------------- //
	// Commands
	// -------------------------------------------- //
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		List<String> parameters = new ArrayList<String>(Arrays.asList(args));
		this.handleCommand(sender, parameters);
		return true;
	}
	
	public void handleCommand(CommandSender sender, List<String> parameters) {
		if (parameters.size() == 0) {
			this.commands.get(0).execute(sender, parameters);
			return;
		}
		
		String commandName = parameters.get(0).toLowerCase();
		parameters.remove(0);
		
		for (FBaseCommand fcommand : this.commands) {
			if (fcommand.getAliases().contains(commandName)) {
				fcommand.execute(sender, parameters);
				return;
			}
		}
		
		sender.sendMessage(Conf.colorSystem+"Unknown faction command \""+commandName+"\". Try /help faction"); // TODO test help messages exists....
		//TODO should we use internal help system instead?
	}
	
	// -------------------------------------------- //
	// Logging
	// -------------------------------------------- //
	public static void log(String msg) {
		log(Level.INFO, msg);
	}
	
	public static void log(Level level, String msg) {
		Logger.getLogger("Minecraft").log(level, "["+instance.getDescription().getFullName()+"] "+msg);
	}

}
