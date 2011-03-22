package com.bukkit.mcteam.factions;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;



import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.bukkit.mcteam.factions.commands.FBaseCommand;
import com.bukkit.mcteam.factions.commands.FCommandAdmin;
import com.bukkit.mcteam.factions.commands.FCommandChat;
import com.bukkit.mcteam.factions.commands.FCommandClaim;
import com.bukkit.mcteam.factions.commands.FCommandCreate;
import com.bukkit.mcteam.factions.commands.FCommandDeinvite;
import com.bukkit.mcteam.factions.commands.FCommandDescription;
import com.bukkit.mcteam.factions.commands.FCommandHelp;
import com.bukkit.mcteam.factions.commands.FCommandInvite;
import com.bukkit.mcteam.factions.commands.FCommandJoin;
import com.bukkit.mcteam.factions.commands.FCommandKick;
import com.bukkit.mcteam.factions.commands.FCommandLeave;
import com.bukkit.mcteam.factions.commands.FCommandList;
import com.bukkit.mcteam.factions.commands.FCommandMap;
import com.bukkit.mcteam.factions.commands.FCommandMod;
import com.bukkit.mcteam.factions.commands.FCommandOpen;
import com.bukkit.mcteam.factions.commands.FCommandRelationAlly;
import com.bukkit.mcteam.factions.commands.FCommandRelationEnemy;
import com.bukkit.mcteam.factions.commands.FCommandRelationNeutral;
import com.bukkit.mcteam.factions.commands.FCommandShow;
import com.bukkit.mcteam.factions.commands.FCommandTag;
import com.bukkit.mcteam.factions.commands.FCommandTitle;
import com.bukkit.mcteam.factions.commands.FCommandUnclaim;
import com.bukkit.mcteam.factions.commands.FCommandVersion;
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

	private String baseCommand;
	
	public Factions() {
		Factions.instance = this;
	}
	
	
	@Override
	public void onEnable() {
		log("=== INIT START ===");
		long timeInitStart = System.currentTimeMillis();
		
		// Add the commands
		commands.add(new FCommandHelp());
		commands.add(new FCommandAdmin());
		commands.add(new FCommandChat());
		commands.add(new FCommandClaim());
		commands.add(new FCommandCreate());
		commands.add(new FCommandDeinvite());
		commands.add(new FCommandDescription());
		commands.add(new FCommandInvite());
		commands.add(new FCommandJoin());
		commands.add(new FCommandKick());
		commands.add(new FCommandLeave());
		commands.add(new FCommandList());
		commands.add(new FCommandMap());
		commands.add(new FCommandMod());
		commands.add(new FCommandOpen());
		commands.add(new FCommandRelationAlly());
		commands.add(new FCommandRelationEnemy());
		commands.add(new FCommandRelationNeutral());
		commands.add(new FCommandShow());
		commands.add(new FCommandTag());
		commands.add(new FCommandTitle());
		commands.add(new FCommandUnclaim());
		commands.add(new FCommandVersion());
		
		// Ensure basefolder exists!
		this.getDataFolder().mkdirs();
		
		Conf.load();
		FPlayer.load();
		Faction.load();
		Board.load();
		
		setupHelp();
		setupPermissions();
		
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
		FPlayer.save();
		Faction.save();
		Board.save();
		log("Disabled");
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
			helpPlugin.registerCommand(this.getBaseCommand()+" help *[page]", "Factions plugin help.", helpPlugin, true);
		}
	}

	
	// -------------------------------------------- //
	// Commands
	// -------------------------------------------- //
	
	@SuppressWarnings("unchecked")
	public String getBaseCommand() {
		if (this.baseCommand != null) {
			return this.baseCommand;
		}
		
		Map<String, Object> Commands = (Map<String, Object>)this.getDescription().getCommands();
		this.baseCommand = Commands.keySet().iterator().next();
		return this.baseCommand;
	}
	
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
		
		sender.sendMessage(Conf.colorSystem+"Unknown faction command \""+commandName+"\". Try "+Conf.colorCommand+"/f help");
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
