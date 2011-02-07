package com.bukkit.mcteam.factions.entities;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.*;

import org.bukkit.entity.Player;

import com.bukkit.mcteam.factions.Factions;
import com.bukkit.mcteam.factions.util.*;
import com.bukkit.mcteam.util.DiscUtil;
import com.google.gson.*;

/**
 * This is a entity manager that persists object to json.
 * Before using the the EM you should always EM.loadAll().
 * The methods assume that all on disc is loaded into memory.
 */
public class EM {
	protected static Map<String, Follower> followers = new HashMap<String, Follower>(); // Where String is a lowercase playername
	protected static Map<Integer, Faction> factions = new HashMap<Integer, Faction>(); // Where Integer is a primary auto increment key
	protected static int nextFactionId;
	
	// hardcoded config
	protected final static String ext = ".json";
	protected final static File folderBase = Factions.folder;
	protected final static File folderFaction = new File(folderBase, "faction");
	protected final static File folderFollower = new File(folderBase, "follower");
	protected final static File fileConfig = new File(folderBase, "conf"+ext);
	protected final static File fileBoard = new File(folderBase, "board"+ext);
	
	public final static Gson gson = new GsonBuilder()
	.setPrettyPrinting()
	.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
	.registerTypeAdapter(Map.class, new MapAsArrayTypeAdapter()) // a "must have" adapter for GSON 
	.create();

	public static void loadAll() {
		folderBase.mkdirs();
		configLoad();
		Log.threshold = Conf.logThreshold;
		boardLoad();
		followerLoadAll();
		factionLoadAll();
	}
	
	//----------------------------------------------//
	// Config methods (load, save)
	//----------------------------------------------//
	public static boolean configLoad() {
		if (fileConfig.exists()) {
			try {
				gson.fromJson(DiscUtil.read(fileConfig), Conf.class);
				Log.info("Config was loaded from disc");
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				Log.warn("Failed to load the config");
				return false;
			}
		}
		Log.info("No conf.json found! Creating a new one with the default values");
		configSave();
		return true;		
	}
	
	public static boolean configSave() {
		folderBase.mkdirs();
		try {
			DiscUtil.write(fileConfig, gson.toJson(new Conf()));
			Log.debug("Config was saved to disc");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			Log.warn("Failed to save the config");
			return false;
		}
	}
	
	//----------------------------------------------//
	// Board methods (load, save)
	//----------------------------------------------//
	public static boolean boardLoad() {
		if (fileBoard.exists()) {
			try {
				gson.fromJson(DiscUtil.read(fileBoard), Board.class);
				Log.info("Board was loaded from disc");
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				Log.warn("Failed to load the board");
				return false;
			}
		}
		Log.info("No board.json found! Creating a new one with the default values");
		boardSave();
		return true;		
	}
	
	public static boolean boardSave() {
		folderBase.mkdirs();
		try {
			DiscUtil.write(fileBoard, gson.toJson(new Board()));
			Log.debug("Board was saved to disc");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			Log.warn("Failed to save the board");
			return false;
		}
	}
	
	//----------------------------------------------//
	// Follower methods (loadAll, get, save)
	//----------------------------------------------//
	
	/**
	 * This method will create a follower entity and assign the link to the corresponding player.
	 */
	public static void onPlayerLogin(Player player) {
		Follower follower = followerGet(player);
		follower.player = player;
	}
	
	public static void onPlayerLogout(Player player) {
		followers.get(player.getName()).player = null;
	}
	
	/**
	 * This method loads all followers from disc into memory.
	 */
	public static void followerLoadAll() {
		Log.info("Loading all followers from disc...");
		folderFollower.mkdirs();
				
		class jsonFileFilter implements FileFilter {
			public boolean accept(File file) {
				return (file.getName().toLowerCase().endsWith(ext) && file.isFile());
			}
		}

		File[] jsonFiles = folderFollower.listFiles(new jsonFileFilter());
		
		for (File jsonFile : jsonFiles) {
			// Extract the name from the filename. The name is filename minus ".json"
			String name = jsonFile.getName();
			name = name.substring(0, name.length() - ext.length());
			try {
				Follower follower = gson.fromJson(DiscUtil.read(jsonFile), Follower.class);
				follower.id = name;
				followers.put(follower.id, follower);
				Log.debug("loaded follower "+name);
			} catch (Exception e) {
				e.printStackTrace();
				Log.warn("failed to load follower "+name);
			}
		}
	}
	
	public static Collection<Follower> followerGetAll() {
		return followers.values();
	}
	
	/**
	 * This method returns the follower object for a player
	 * A new Follower will be created if the player did not have one
	 */
	public static Follower followerGet(Player player) {
		String key = followerKey(player);
		
		if (followers.containsKey(key)) {
			return followers.get(key);
		}
		
		return followerCreate(player);
	}
	
	public static boolean followerSave(String id) {
		Object obj = followers.get(id);
		if (obj == null) {
			Log.warn("Could not save follower "+id+" as it was not loaded");
			return false;
		}
		folderFollower.mkdirs();
		File file = new File(folderFollower, id+ext);
		try {
			DiscUtil.write(file, gson.toJson(obj));
			Log.debug("Saved the follower "+id);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			Log.warn("Failed to save the follower "+id);
			return false;
		}
		
		
	}
	
	protected static String followerKey(Player player) {
		return player.getName();
	}
	
	protected static Follower followerCreate(Player player) {
		Log.debug("Creating new follower "+followerKey(player));
		Follower follower = new Follower();
		follower.id = followerKey(player);
		followers.put(follower.id, follower);
		follower.save();
		return follower;
	}
	
	//----------------------------------------------//
	// Faction methods (loadAll, get, create, delete, save)
	//----------------------------------------------//
	
	/**
	 * This method loads all followers from disc into memory.
	 */
	public static void factionLoadAll() {
		Log.info("Loading all factions from disc...");
		folderFaction.mkdirs();
				
		class jsonFileFilter implements FileFilter
		{
			public boolean accept(File file) {
				return (file.getName().toLowerCase().endsWith(ext) && file.isFile());
			}
		}

		nextFactionId = 0;
		File[] jsonFiles = folderFaction.listFiles(new jsonFileFilter());
		for (File jsonFile : jsonFiles) {
			// Extract the name from the filename. The name is filename minus ".json"
			String name = jsonFile.getName();
			name = name.substring(0, name.length() - ext.length());
			int id = Integer.parseInt(name);
			
			// Eventually push next id forward
			if (nextFactionId < id) {
				nextFactionId = id;
			}
			
			try {
				Faction faction = gson.fromJson(DiscUtil.read(jsonFile), Faction.class);
				faction.id = id;
				factions.put(faction.id, faction);
				Log.debug("loaded faction "+id);
			} catch (Exception e) {
				e.printStackTrace();
				Log.warn("Failed to load faction "+id);
			}
		}
		
		nextFactionId += 1; // make it the next id and not the current highest.
		
		// Make sure the default neutral faction exists
		if ( ! factions.containsKey(0)) {
			Faction faction = new Faction();
			faction.name = "*No faction*";
			faction.description = "\"The faction for the factionless :P\"";
			faction.id = 0;
			factions.put(faction.id, faction);
		}
	}
	
	public static Faction factionGet(Integer factionId) {
		return factions.get(factionId);
	}
	
	public static Collection<Faction> factionGetAll() {
		return factions.values();
	}
	
	public static Faction factionCreate(){
		Faction faction = new Faction();
		faction.id = nextFactionId;
		nextFactionId += 1;
		factions.put(faction.id, faction);
		Log.debug("created new faction "+faction.id);
		faction.save();
		return faction;
	}
	
	public static boolean factionDelete(Integer id) {
		// NOTE that this does not do any security checks.
		// Follower might get orphaned foreign id's
		
		// purge from board
		Board.purgeFaction(id);
		
		// Remove the file
		File file = new File(folderFaction, id+ext);
		file.delete();
		
		// Remove the faction
		factions.remove(id);
		
		return true; // TODO
	}
	
	public static boolean factionSave(Integer id) {
		Object obj = factions.get(id);
		if (obj == null) {
			Log.warn("Could not save faction "+id+" as it was not loaded");
			return false;
		}
		folderFaction.mkdirs();
		File file = new File(folderFaction, id+ext);
		try {
			DiscUtil.write(file, gson.toJson(obj));
			Log.debug("saved the faction "+id);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			Log.warn("failed to save the faction "+id);
			return false;
		}
	}
}
