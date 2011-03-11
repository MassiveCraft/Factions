package com.bukkit.mcteam.factions.entities;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.*;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.bukkit.mcteam.factions.Factions;
import com.bukkit.mcteam.factions.util.*;
import com.bukkit.mcteam.util.DiscUtil;
import com.bukkit.mcteam.gson.*;

/**
 * This is a entity manager that persists object to json.
 * Before using the the EM you should always EM.loadAll().
 * The methods assume that all on disc is loaded into memory.
 */
public class EM {
	protected static Map<String, Follower> followers = new HashMap<String, Follower>(); // Where String is a lowercase playername
	protected static Map<Integer, Faction> factions = new HashMap<Integer, Faction>(); // Where Integer is a primary auto increment key
	protected static Map<String, Board> boards = new HashMap<String, Board>(); // Where Long is the semi (sadly) unique world id.
	protected static int nextFactionId;
	
	// hardcoded config
	protected final static String ext = ".json";
	protected final static File folderBase = Factions.factions.getDataFolder();
	protected final static File folderFaction = new File(folderBase, "faction");
	protected final static File folderFollower = new File(folderBase, "follower");
	protected final static File folderBoard = new File(folderBase, "board");
	protected final static File fileConfig = new File(folderBase, "conf"+ext);
	
	public final static Gson gson = new GsonBuilder()
	.setPrettyPrinting()
	.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
	.registerTypeAdapter(Map.class, new MapAsArrayTypeAdapter()) // a "must have" adapter for GSON 
	.create();

	public static void loadAll() {
		folderBase.mkdirs();
		configLoad();
		Log.threshold = Conf.logThreshold;
		factionLoadAll();
		followerLoadAll();
		boardLoadAll();
		Board.cleanAll();
	}
	
	//----------------------------------------------//
	// Config methods (load, save)
	//----------------------------------------------//
	public static boolean configLoad() {
		if (fileConfig.exists()) {
			try {
				gson.fromJson(DiscUtil.read(fileConfig), Conf.class);
				Log.info("Config was loaded from disk");
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
	/*public static boolean boardLoad() {
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
	}*/
	
	//----------------------------------------------//
	// Board methods (loadAll, get, save)
	//----------------------------------------------//
	
	/**
	 * This method loads all boards from disc into memory.
	 */
	public static void boardLoadAll() {
		Log.info("Loading all boards from disk...");
		folderBoard.mkdirs();
				
		class jsonFileFilter implements FileFilter {
			@Override
			public boolean accept(File file) {
				return (file.getName().toLowerCase().endsWith(ext) && file.isFile());
			}
		}

		File[] jsonFiles = folderBoard.listFiles(new jsonFileFilter());
		
		for (File jsonFile : jsonFiles) {
			// Extract the name from the filename. The name is filename minus ".json"
			String name = jsonFile.getName();
			name = name.substring(0, name.length() - ext.length());
			try {
				Board board = gson.fromJson(DiscUtil.read(jsonFile), Board.class);
				board.worldName = name;
				boards.put(board.worldName, board);
				Log.debug("loaded board "+name);
			} catch (Exception e) {
				e.printStackTrace();
				Log.warn("failed to load board "+name);
			}
		}
	}
	
	public static Collection<Board> boardGetAll() {
		return boards.values();
	}
	
	/**
	 * This method returns the board object for a world
	 * A new Board will be created if the world did not have one
	 */
	public static Board boardGet(World world) {
		if (boards.containsKey(world.getName())) {
			return boards.get(world.getName());
		}
		
		return boardCreate(world);
	}
	
	public static boolean boardSave(String worldName) {
		Object obj = boards.get(worldName);
		if (obj == null) {
			Log.warn("Could not save board "+worldName+" as it was not loaded");
			return false;
		}
		folderBoard.mkdirs();
		File file = new File(folderBoard, worldName+ext);
		try {
			DiscUtil.write(file, gson.toJson(obj));
			Log.debug("Saved the board "+worldName);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			Log.warn("Failed to save the board "+worldName);
			return false;
		}		
	}
	
	protected static Board boardCreate(World world) {
		Log.debug("Creating new board for "+world.getName());
		Board board = new Board();
		board.worldName = world.getName();
		boards.put(board.worldName, board);
		board.save();
		return board;
	}
	
	//----------------------------------------------//
	// Follower methods (loadAll, get, save)
	//----------------------------------------------//
	
	/**
	 * This method loads all followers from disc into memory.
	 */
	public static void followerLoadAll() {
		Log.info("Loading all followers from disk...");
		folderFollower.mkdirs();
				
		class jsonFileFilter implements FileFilter {
			@Override
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
				//Log.debug("loaded follower "+name);
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
		Log.info("Loading all factions from disk...");
		folderFaction.mkdirs();
				
		class jsonFileFilter implements FileFilter
		{
			@Override
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
				//Log.debug("loaded faction "+id);
			} catch (Exception e) {
				e.printStackTrace();
				Log.warn("Failed to load faction "+id);
			}
		}
		
		nextFactionId += 1; // make it the next id and not the current highest.
		
		// Make sure the default neutral faction exists
		if ( ! factions.containsKey(0)) {
			Faction faction = new Faction();
			faction.tag = "*No faction*";
			faction.description = "\"The faction for the factionless :P\"";
			faction.id = 0;
			factions.put(faction.id, faction);
		}
	}
	
	public static Faction factionGet(Integer factionId) {
		if ( ! factions.containsKey(factionId)) {
			Log.warn("Non existing factionId "+factionId+" requested from EM! Issuing board cleaning!");
			Board.cleanAll();
		}
		return factions.get(factionId);
	}
	
	public static boolean factionExists(Integer factionId) {
		return factions.containsKey(factionId);
	}
	
	public static Collection<Faction> factionGetAll() {
		return factions.values();
	}
	
	public static Faction factionCreate() {
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
		
		// purge from all boards
		//Board.purgeFactionFromAllBoards(id);
		Board.cleanAll();
		
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
