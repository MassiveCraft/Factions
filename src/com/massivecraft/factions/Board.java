package com.massivecraft.factions;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.ChatColor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.AsciiCompass;
import com.massivecraft.factions.util.DiscUtil;
import com.massivecraft.factions.util.TextUtil;


public class Board {
	private static transient File file = new File(Factions.instance.getDataFolder(), "board.json");
	private static transient HashMap<FLocation, Integer> flocationIds = new HashMap<FLocation, Integer>();
	
	//----------------------------------------------//
	// Get and Set
	//----------------------------------------------//
	public static int getIdAt(FLocation flocation) {
		if ( ! flocationIds.containsKey(flocation)) {
			return 0;
		}
		
		return flocationIds.get(flocation);
	}
	
	public static Faction getFactionAt(FLocation flocation) {
		return Faction.get(getIdAt(flocation));
	}
	
	public static void setIdAt(int id, FLocation flocation) {
		clearOwnershipAt(flocation);

		if (id == 0) {
			removeAt(flocation);
		}
		
		flocationIds.put(flocation, id);
	}
	
	public static void setFactionAt(Faction faction, FLocation flocation) {
		setIdAt(faction.getId(), flocation);
	}
	
	public static void removeAt(FLocation flocation) {
		clearOwnershipAt(flocation);
		flocationIds.remove(flocation);
	}
	
	// not to be confused with claims, ownership referring to further member-specific ownership of a claim
	public static void clearOwnershipAt(FLocation flocation) {
		Faction faction = getFactionAt(flocation);
		if (faction != null && faction.isNormal()) {
			faction.clearClaimOwnership(flocation);
		}
	}
	
	public static void unclaimAll(int factionId) {
		Faction faction = Faction.get(factionId);
		if (faction != null && faction.isNormal()) {
			faction.clearAllClaimOwnership();
		}

		Iterator<Entry<FLocation, Integer>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<FLocation, Integer> entry = iter.next();
			if (entry.getValue().equals(factionId)) {
				iter.remove();
			}
		}
	}

	// Is this coord NOT completely surrounded by coords claimed by the same faction?
	// Simpler: Is there any nearby coord with a faction other than the faction here?
	public static boolean isBorderLocation(FLocation flocation) {
		Faction faction = getFactionAt(flocation);
		FLocation a = flocation.getRelative(1, 0);
		FLocation b = flocation.getRelative(-1, 0);
		FLocation c = flocation.getRelative(0, 1);
		FLocation d = flocation.getRelative(0, -1);
		return faction != getFactionAt(a) || faction != getFactionAt(b) || faction != getFactionAt(c) || faction != getFactionAt(d);
	}

	// Is this coord connected to any coord claimed by the specified faction?
	public static boolean isConnectedLocation(FLocation flocation, Faction faction) {
		FLocation a = flocation.getRelative(1, 0);
		FLocation b = flocation.getRelative(-1, 0);
		FLocation c = flocation.getRelative(0, 1);
		FLocation d = flocation.getRelative(0, -1);
		return faction == getFactionAt(a) || faction == getFactionAt(b) || faction == getFactionAt(c) || faction == getFactionAt(d);
	}
	
	
	//----------------------------------------------//
	// Cleaner. Remove orphaned foreign keys
	//----------------------------------------------//
	
	public static void clean() {
		Iterator<Entry<FLocation, Integer>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<FLocation, Integer> entry = iter.next();
			if ( ! Faction.exists(entry.getValue())) {
				Factions.log("Board cleaner removed "+entry.getValue()+" from "+entry.getKey());
				iter.remove();
			}
		}
	}	
	
	//----------------------------------------------//
	// Coord count
	//----------------------------------------------//
	
	public static int getFactionCoordCount(int factionId) {
		int ret = 0;
		for (int thatFactionId : flocationIds.values()) {
			if(thatFactionId == factionId) {
				ret += 1;
			}
		}
		return ret;
	}
	
	public static int getFactionCoordCount(Faction faction) {
		return getFactionCoordCount(faction.getId());
	}
	
	public static int getFactionCoordCountInWorld(Faction faction, String worldName) {
		int factionId = faction.getId();
		int ret = 0;
		Iterator<Entry<FLocation, Integer>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<FLocation, Integer> entry = iter.next();
			if (entry.getValue() == factionId && entry.getKey().getWorldName().equals(worldName)) {
				ret += 1;
			}
		}
		return ret;
	}
	
	//----------------------------------------------//
	// Map generation
	//----------------------------------------------//
	
	/**
	 * The map is relative to a coord and a faction
	 * north is in the direction of decreasing x
	 * east is in the direction of decreasing z
	 */
	public static ArrayList<String> getMap(Faction faction, FLocation flocation, double inDegrees) {
		ArrayList<String> ret = new ArrayList<String>();
		Faction factionLoc = getFactionAt(flocation);
		ret.add(TextUtil.titleize("("+flocation.getCoordString()+") "+factionLoc.getTag(faction)));
		
		int halfWidth = Conf.mapWidth / 2;
		int halfHeight = Conf.mapHeight / 2;
		FLocation topLeft = flocation.getRelative(-halfHeight, halfWidth);
		int width = halfWidth * 2 + 1;
		int height = halfHeight * 2 + 1;
		
		if (Conf.showMapFactionKey) {
			height--;
		}
		
		Map<String, Character> fList = new HashMap<String, Character>();
		int chrIdx = 0;
		
		// For each row
		for (int dx = 0; dx < height; dx++) {
			// Draw and add that row
			String row = "";
			for (int dz = 0; dz > -width; dz--) {
				if(dz == -(halfWidth) && dx == halfHeight) {
					row += ChatColor.AQUA+"+";
				} else {
					FLocation flocationHere = topLeft.getRelative(dx, dz);
					Faction factionHere = getFactionAt(flocationHere);
					Relation relation = faction.getRelation(factionHere);
					if (factionHere.isNone()) {
						row += ChatColor.GRAY+"-";
					} else if (factionHere.isSafeZone()) {
						row += ChatColor.GOLD+"+";
					} else if (factionHere.isWarZone()) {
						row += ChatColor.DARK_RED+"+";
					} else if (
						   factionHere == faction
						|| factionHere == factionLoc
						|| relation.isAtLeast(Relation.ALLY)
						|| (Conf.showNeutralFactionsOnMap && relation.equals(Relation.NEUTRAL))
						|| (Conf.showEnemyFactionsOnMap && relation.equals(Relation.ENEMY))
						) {
						if (!fList.containsKey(factionHere.getTag()))
							fList.put(factionHere.getTag(), Conf.mapKeyChrs[chrIdx++]);
						char tag = fList.get(factionHere.getTag());
						row += factionHere.getRelation(faction).getColor() + "" + tag;
					} else {
						row += ChatColor.GRAY+"-";
					}
				}
			}
			ret.add(row);
		}
		
		// Get the compass
		ArrayList<String> asciiCompass = AsciiCompass.getAsciiCompass(inDegrees, ChatColor.RED, Conf.colorChrome);

		// Add the compass
		ret.set(1, asciiCompass.get(0)+ret.get(1).substring(3*3));
		ret.set(2, asciiCompass.get(1)+ret.get(2).substring(3*3));
		ret.set(3, asciiCompass.get(2)+ret.get(3).substring(3*3));
		
		// Add the faction key
		if (Conf.showMapFactionKey) {
			String fRow = "";
			for(String key : fList.keySet()) {
				fRow += String.format("%s%s: %s ", ChatColor.GRAY, fList.get(key), key);
			}
			ret.add(fRow);
		}
		
		return ret;
	}
	
	
	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	
	public static Map<String,Map<String,Integer>> dumpAsSaveFormat() {
		Map<String,Map<String,Integer>> worldCoordIds = new HashMap<String,Map<String,Integer>>(); 
		
		String worldName, coords;
		Integer id;
		
		for (Entry<FLocation, Integer> entry : flocationIds.entrySet()) {
			worldName = entry.getKey().getWorldName();
			coords = entry.getKey().getCoordString();
			id = entry.getValue();
			if ( ! worldCoordIds.containsKey(worldName)) {
				worldCoordIds.put(worldName, new TreeMap<String,Integer>());
			}
			
			worldCoordIds.get(worldName).put(coords, id);
		}
		
		return worldCoordIds;
	}
	
	public static void loadFromSaveFormat(Map<String,Map<String,Integer>> worldCoordIds) {
		flocationIds.clear();
		
		String worldName;
		String[] coords;
		int x, z, factionId;
		
		for (Entry<String,Map<String,Integer>> entry : worldCoordIds.entrySet()) {
			worldName = entry.getKey();
			for (Entry<String,Integer> entry2 : entry.getValue().entrySet()) {
				coords = entry2.getKey().trim().split("[,\\s]+");
				x = Integer.parseInt(coords[0]);
				z = Integer.parseInt(coords[1]);
				factionId = entry2.getValue();
				flocationIds.put(new FLocation(worldName, x, z), factionId);
			}
		}
	}
	
	public static boolean save() {
		//Factions.log("Saving board to disk");
		
		try {
			DiscUtil.write(file, Factions.instance.gson.toJson(dumpAsSaveFormat()));
		} catch (Exception e) {
			e.printStackTrace();
			Factions.log("Failed to save the board to disk.");
			return false;
		}
		
		return true;
	}
	
	public static boolean load() {
		Factions.log("Loading board from disk");
		
		if ( ! file.exists()) {
			if ( ! loadOld())
				Factions.log("No board to load from disk. Creating new file.");
			save();
			return true;
		}
		
		try {
			Type type = new TypeToken<Map<String,Map<String,Integer>>>(){}.getType();
			Map<String,Map<String,Integer>> worldCoordIds = Factions.instance.gson.fromJson(DiscUtil.read(file), type);
			loadFromSaveFormat(worldCoordIds);
		} catch (Exception e) {
			e.printStackTrace();
			Factions.log("Failed to load the board from disk.");
			return false;
		}
			
		return true;
	}

	private static boolean loadOld() {
		File folderBoard = new File(Factions.instance.getDataFolder(), "board");

		if ( ! folderBoard.isDirectory())
			return false;

		Factions.log("Board file doesn't exist, attempting to load old pre-1.1 data.");

		String ext = ".json";

		class jsonFileFilter implements FileFilter {
			@Override
			public boolean accept(File file) {
				return (file.getName().toLowerCase().endsWith(".json") && file.isFile());
			}
		}

		File[] jsonFiles = folderBoard.listFiles(new jsonFileFilter());
		for (File jsonFile : jsonFiles) {
			// Extract the name from the filename. The name is filename minus ".json"
			String name = jsonFile.getName();
			name = name.substring(0, name.length() - ext.length());
			try {
				JsonParser parser = new JsonParser();
				JsonObject json = (JsonObject) parser.parse(DiscUtil.read(jsonFile));
				JsonArray coords = json.getAsJsonArray("coordFactionIds");
				Iterator<JsonElement> coordSet = coords.iterator();
				while(coordSet.hasNext()) {
					JsonArray coordDat = (JsonArray) coordSet.next();
					JsonObject coord = coordDat.get(0).getAsJsonObject();
					int coordX = coord.get("x").getAsInt();
					int coordZ = coord.get("z").getAsInt();
					int factionId = coordDat.get(1).getAsInt();
					flocationIds.put(new FLocation(name, coordX, coordZ), factionId);
				}
				Factions.log("loaded pre-1.1 board "+name);
			} catch (Exception e) {
				e.printStackTrace();
				Factions.log(Level.WARNING, "failed to load board "+name);
			}
		}
		return true;
	}
}



















