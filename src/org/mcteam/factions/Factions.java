package org.mcteam.factions;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;



import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcteam.factions.commands.*;
import org.mcteam.factions.gson.Gson;
import org.mcteam.factions.gson.GsonBuilder;
import org.mcteam.factions.listeners.FactionsBlockListener;
import org.mcteam.factions.listeners.FactionsEntityListener;
import org.mcteam.factions.listeners.FactionsPlayerListener;


import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * The data is saved to disk every 30min and on plugin disable.
 */
public class Factions extends JavaPlugin {
	// -------------------------------------------- //
	// Fields
	// -------------------------------------------- //
	public static Factions instance;
	private Integer saveTask = null;
	
	public final static Gson gson = new GsonBuilder()
	.setPrettyPrinting()
	.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
	.registerTypeAdapter(Location.class, new MyLocationTypeAdapter())
	.create();
	
	private final FactionsPlayerListener playerListener = new FactionsPlayerListener();
	private final FactionsEntityListener entityListener = new FactionsEntityListener();
	private final FactionsBlockListener blockListener = new FactionsBlockListener();
	
	public static PermissionHandler Permissions;

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
		commands.add(new FCommandBypass());
		commands.add(new FCommandChat());
		commands.add(new FCommandClaim());
		commands.add(new FCommandCreate());
		commands.add(new FCommandDeinvite());
		commands.add(new FCommandDescription());
		commands.add(new FCommandDisband());
		commands.add(new FCommandHome());
		commands.add(new FCommandInvite());
		commands.add(new FCommandJoin());
		commands.add(new FCommandKick());
		commands.add(new FCommandLeave());
		commands.add(new FCommandList());
		commands.add(new FCommandLock());
		commands.add(new FCommandMap());
		commands.add(new FCommandMod());
		commands.add(new FCommandOpen());
		commands.add(new FCommandRelationAlly());
		commands.add(new FCommandRelationEnemy());
		commands.add(new FCommandRelationNeutral());
		commands.add(new FCommandReload());
		commands.add(new FCommandSafeclaim());
		commands.add(new FCommandSafeunclaimall());
		commands.add(new FCommandSaveAll());
		commands.add(new FCommandSethome());
		commands.add(new FCommandShow());
		commands.add(new FCommandTag());
		commands.add(new FCommandTitle());
		commands.add(new FCommandUnclaim());
		commands.add(new FCommandUnclaimall());
		commands.add(new FCommandVersion());
		commands.add(new FCommandWarclaim());
		commands.add(new FCommandWarunclaimall());
		commands.add(new FCommandWorldNoClaim());
		commands.add(new FCommandWorldNoPowerLoss());
		
		// Ensure base folder exists!
		this.getDataFolder().mkdirs();
		
		Conf.load();
		FPlayer.load();
		Faction.load();
		Board.load();
		
		setupPermissions();
		
		// preload some chat plugins if they're on the server to prevent potential conflicts
		if (Conf.preloadChatPlugins) {
			preloadPlugin("Essentials");
			preloadPlugin("EssentialsChat");
			preloadPlugin("HeroChat");
			preloadPlugin("iChat");
		}
		
		// Register events
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, this.playerListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_FILL, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.CREATURE_SPAWN, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_TARGET, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PAINTING_BREAK, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PAINTING_PLACE, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGE, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, this.blockListener, Event.Priority.Normal, this);
		
		// Register recurring tasks
		long saveTicks = 20 * 60 * 30; // Approximately every 30 min
		if (saveTask == null)
			saveTask = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new SaveTask(), saveTicks, saveTicks);
		
		log("=== INIT DONE (Took "+(System.currentTimeMillis()-timeInitStart)+"ms) ===");
	}

	@Override
	public void onDisable() {
		if (saveTask != null) {
			this.getServer().getScheduler().cancelTask(saveTask);
			saveTask = null;
		}
		saveAll();
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
	
	private void preloadPlugin(String pluginName) {
		PluginManager pm = this.getServer().getPluginManager();
		Plugin prePlug = pm.getPlugin(pluginName);
		
		if (prePlug != null && !pm.isPluginEnabled(prePlug)) {
			Factions.log("Preloading \"" + pluginName + "\" plugin to prevent conflicts.");
			pm.enablePlugin(prePlug);
		}
	}
	
	// -------------------------------------------- //
	// Test rights
	// -------------------------------------------- //
	
	public static boolean hasPermParticipate(CommandSender sender) {
		return hasPerm(sender, "factions.participate", false);
	}
	
	public static boolean hasPermCreate(CommandSender sender) {
		return hasPerm(sender, "factions.create", false);
	}
	
	public static boolean hasPermManageSafeZone(CommandSender sender) {
		return hasPerm(sender, "factions.manageSafeZone", true);
	}
	
	public static boolean hasPermManageWarZone(CommandSender sender) {
		return hasPerm(sender, "factions.manageWarZone", true);
	}

	public static boolean hasPermAdminBypass(CommandSender sender) {
		return hasPerm(sender, "factions.adminBypass", true);
	}
	
	public static boolean hasPermReload(CommandSender sender) {
		return hasPerm(sender, "factions.reload", true);
	}
	
	public static boolean hasPermSaveAll(CommandSender sender) {
		return hasPerm(sender, "factions.saveall", true);
	}
	
	public static boolean hasPermLock(CommandSender sender) {
		return hasPerm(sender, "factions.lock", true);
	}
	
	public static boolean hasPermDisband(CommandSender sender) {
		return hasPerm(sender, "factions.disband", true);
	}
	
	public static boolean hasPermWorlds(CommandSender sender) {
		return hasPerm(sender, "factions.worldOptions", true);
	}
	
	private static boolean hasPerm(CommandSender sender, String permNode, boolean fallbackOnlyOp) {
		if (Factions.Permissions == null || ! (sender instanceof Player)) {
			return fallbackOnlyOp == false || sender.isOp();
		}
		
		if (sender instanceof Player) {
			Player player = (Player)sender;
			return Factions.Permissions.has(player, permNode); 
		}
		
		return false;
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
		
		sender.sendMessage(Conf.colorSystem+"Unknown faction command \""+commandName+"\". Try "+Conf.colorCommand+"/"+this.getBaseCommand()+" help");
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
	
	// -------------------------------------------- //
	// Save all
	// -------------------------------------------- //
	
	public static void saveAll() {
		FPlayer.save();
		Faction.save();
		Board.save();
		Conf.save();
	}
	
}
