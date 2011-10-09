package com.massivecraft.factions.zcore;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.zcore.persist.EM;
import com.massivecraft.factions.zcore.persist.SaveTask;
import com.massivecraft.factions.zcore.util.LibLoader;
import com.massivecraft.factions.zcore.util.PermUtil;
import com.massivecraft.factions.zcore.util.Persist;
import com.massivecraft.factions.zcore.util.TextUtil;


public abstract class MPlugin extends JavaPlugin
{
	// Some utils
	public Persist persist;
	public TextUtil txt;
	public LibLoader lib;
	public PermUtil perm;
	
	// Persist related
	public Gson gson;	
	private Integer saveTask = null;
	private boolean autoSave = true;
	public boolean getAutoSave() {return this.autoSave;}
	public void setAutoSave(boolean val) {this.autoSave = val;}
	
	// Listeners
	private MPluginSecretPlayerListener mPluginSecretPlayerListener; 
	private MPluginSecretServerListener mPluginSecretServerListener;
	
	// Our stored base commands
	private List<MCommand<?>> baseCommands = new ArrayList<MCommand<?>>();
	public List<MCommand<?>> getBaseCommands() { return this.baseCommands; }

	// -------------------------------------------- //
	// ENABLE
	// -------------------------------------------- //
	private long timeEnableStart;
	public boolean preEnable()
	{
		log("=== ENABLE START ===");
		timeEnableStart = System.currentTimeMillis();
		
		// Ensure basefolder exists!
		this.getDataFolder().mkdirs();
		
		// Create Utility Instances
		this.perm = new PermUtil(this);
		this.persist = new Persist(this);
		this.lib = new LibLoader(this);
		
		if ( ! lib.require("gson.jar", "http://search.maven.org/remotecontent?filepath=com/google/code/gson/gson/1.7.1/gson-1.7.1.jar")) return false;
		this.gson = this.getGsonBuilder().create();
		
		initTXT();
		
		// Create and register listeners
		this.mPluginSecretPlayerListener = new MPluginSecretPlayerListener(this);
		this.mPluginSecretServerListener = new MPluginSecretServerListener(this);
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_PRELOGIN, this.mPluginSecretPlayerListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.mPluginSecretPlayerListener, Event.Priority.Low, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.mPluginSecretPlayerListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.SERVER_COMMAND, this.mPluginSecretServerListener, Event.Priority.Lowest, this);
		
		
		// Register recurring tasks
		long saveTicks = 20 * 60 * 30; // Approximately every 30 min
		if (saveTask == null)
		{
			saveTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new SaveTask(this), saveTicks, saveTicks);
		}
		
		return true;
	}
	
	public void postEnable()
	{
		log("=== ENABLE DONE (Took "+(System.currentTimeMillis()-timeEnableStart)+"ms) ===");
	}
	
	public void onDisable()
	{
		if (saveTask != null)
		{
			this.getServer().getScheduler().cancelTask(saveTask);
			saveTask = null;
		}
		EM.saveAllToDisc();
		log("Disabled");
	}
	
	public void suicide()
	{
		log("Now I suicide!");
		this.getServer().getPluginManager().disablePlugin(this);
	}

	// -------------------------------------------- //
	// Some inits...
	// You are supposed to override these in the plugin if you aren't satisfied with the defaults
	// The goal is that you always will be satisfied though.
	// -------------------------------------------- //

	public GsonBuilder getGsonBuilder()
	{
		return new GsonBuilder()
		.setPrettyPrinting()
		.disableHtmlEscaping()
		.serializeNulls()
		.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
	}
	
	// -------------------------------------------- //
	// LANG AND TAGS
	// -------------------------------------------- //
	
	// These are not supposed to be used directly.
	// They are loaded and used through the TextUtil instance for the plugin.
	public Map<String, String> tags = new LinkedHashMap<String, String>();
	
	public void addTags()
	{
		this.tags.put("black", "§0");
		this.tags.put("navy", "§1");
		this.tags.put("green", "§2");
		this.tags.put("teal", "§3");
		this.tags.put("red", "§4");
		this.tags.put("purple", "§5");
		this.tags.put("gold", "§6");
		this.tags.put("silver", "§7");
		this.tags.put("gray", "§8");
		this.tags.put("blue", "§9");
		this.tags.put("white", "§f");
		this.tags.put("lime", "§a");
		this.tags.put("aqua", "§b");
		this.tags.put("rose", "§c");
		this.tags.put("pink", "§d");
		this.tags.put("yellow", "§e");
		
		this.tags.put("l", "§2"); // logo
		this.tags.put("a", "§6"); // art
		this.tags.put("n", "§7"); // notice
		this.tags.put("i", "§e"); // info
		this.tags.put("g", "§a"); // good
		this.tags.put("b", "§c"); // bad
		this.tags.put("h", "§d"); // highligh
		this.tags.put("c", "§b"); // command
		this.tags.put("p", "§3"); // parameter
	}
	
	public void initTXT()
	{
		this.addTags();
		
		Type type = new TypeToken<Map<String, String>>(){}.getType();
		
		Map<String, String> tagsFromFile = this.persist.load(type, "tags");
		if (tagsFromFile != null) this.tags.putAll(tagsFromFile);
		this.persist.save(this.tags, "tags");
		
		this.txt = new TextUtil(this.tags);
	}
	
	
	// -------------------------------------------- //
	// COMMAND HANDLING
	// -------------------------------------------- //

	public boolean handleCommand(CommandSender sender, String commandString, boolean testOnly)
	{
		boolean noSlash = false;
		if (commandString.startsWith("/"))
		{
			noSlash = true;
			commandString = commandString.substring(1);
		}
		
		for (MCommand<?> command : this.getBaseCommands())
		{
			if (noSlash && ! command.allowNoSlashAccess) continue;
			
			for (String alias : command.aliases)
			{
				if (commandString.startsWith(alias+" ") || commandString.equals(alias))
				{
					List<String> args = new ArrayList<String>(Arrays.asList(commandString.split("\\s+")));
					args.remove(0);
					if (testOnly) return true;
					command.execute(sender, args);
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean handleCommand(CommandSender sender, String commandString)
	{
		return this.handleCommand(sender, commandString, false);
	}
	
	// -------------------------------------------- //
	// HOOKS
	// -------------------------------------------- //
	public void preAutoSave()
	{
		
	}
	
	public void postAutoSave()
	{
		
	}
	
	// -------------------------------------------- //
	// LOGGING
	// -------------------------------------------- //
	public void log(Object msg)
	{
		log(Level.INFO, msg);
	}
	
	public void log(Level level, Object msg)
	{
		Logger.getLogger("Minecraft").log(level, "["+this.getDescription().getFullName()+"] "+msg);
	}
}
