package com.bukkit.mcteam.factions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

import com.bukkit.mcteam.factions.util.TextUtil;
import com.bukkit.mcteam.gson.reflect.TypeToken;
import com.bukkit.mcteam.util.AsciiCompass;
import com.bukkit.mcteam.util.DiscUtil;

//import com.bukkit.mcteam.factions.util.*;

public class Board {
	protected static transient File file = new File(Factions.instance.getDataFolder(), "board.json");
	private static Map<String,Map<String,Integer>> worldCoordIds = new HashMap<String,Map<String,Integer>>(); 
	
	//----------------------------------------------//
	// Get and Set
	//----------------------------------------------//
	public static int getIdAt(FLocation flocation) {
		if ( ! worldCoordIds.containsKey(flocation.getWorldName())) {
			return 0;
		}
		
		if ( ! worldCoordIds.get(flocation.getWorldName()).containsKey(flocation.getCoordString()) ) {
			return 0;
		}
		
		return worldCoordIds.get(flocation.getWorldName()).get(flocation.getCoordString());
	}
	
	public static Faction getFactionAt(FLocation flocation) {
		return Faction.get(getIdAt(flocation));
	}
	
	public static void setIdAt(int id, FLocation flocation) {
		if (id == 0) {
			removeAt(flocation);
		}
		
		if ( ! worldCoordIds.containsKey(flocation.getWorldName())) {
			worldCoordIds.put(flocation.getWorldName(), new HashMap<String,Integer>());
		}
		
		worldCoordIds.get(flocation.getWorldName()).put(flocation.getCoordString(), id);
		save();
	}
	
	public static void setFactionAt(Faction faction, FLocation flocation) {
		setIdAt(faction.getId(), flocation);
	}
	
	public static void removeAt(FLocation flocation) {
		if ( ! worldCoordIds.containsKey(flocation.getWorldName())) {
			return;
		}
		worldCoordIds.get(flocation.getWorldName()).remove(flocation.getCoordString());
		save();
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
	
	
	//----------------------------------------------//
	// Cleaner. Remove orphaned foreign keys
	//----------------------------------------------//
	
	public static void clean() {
		for (String worldName : worldCoordIds.keySet()) {
			Iterator<Entry<String, Integer>> iter = worldCoordIds.get(worldName).entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, Integer> entry = iter.next();
				if ( ! Faction.exists(entry.getValue())) {
					Factions.log("Board cleaner removed non existing faction id "+entry.getValue()+" from "+worldName+" "+entry.getKey());
					iter.remove();
				}
			}
		}
	}	
	
	//----------------------------------------------//
	// Coord count
	//----------------------------------------------//
	
	public static int getFactionCoordCount(int factionId) {
		int ret = 0;
		for (Map<String, Integer> coordIds : worldCoordIds.values()) {
			for (int thatFactionId : coordIds.values()) {
				if(thatFactionId == factionId) {
					ret += 1;
				}
			}
		}
		return ret;
	}
	
	public static int getFactionCoordCount(Faction faction) {
		return getFactionCoordCount(faction.getId());
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
		ret.add(TextUtil.titleize("("+flocation.getCoordString()+") "+getFactionAt(flocation).getTag(faction)));
		
		int halfWidth = Conf.mapWidth / 2;
		int halfHeight = Conf.mapHeight / 2;
		FLocation topLeft = flocation.getRelative(-halfHeight, halfWidth);
		int width = halfWidth * 2 + 1;
		int height = halfHeight * 2 + 1;
		
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
					if (factionHere.getId() == 0) {
						row += ChatColor.GRAY+"-";
					} else {
						row += factionHere.getRelation(faction).getColor()+"+";
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
		
		return ret;
	}
	
	
	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	
	public static boolean save() {
		Factions.log("Saving board to disk");
		
		try {
			DiscUtil.write(file, Factions.gson.toJson(worldCoordIds));
		} catch (IOException e) {
			Factions.log("Failed to save the board to disk.");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean load() {
		if ( ! file.exists()) {
			Factions.log("No board to load from disk. Creating new file.");
			save();
			return true;
		}
		
		try {
			Type type = new TypeToken<Map<String,Map<String,Integer>>>(){}.getType();
			worldCoordIds = Factions.gson.fromJson(DiscUtil.read(file), type);
		} catch (IOException e) {
			Factions.log("Failed to load the board from disk.");
			e.printStackTrace();
			return false;
		}
			
		return true;
	}
}



















