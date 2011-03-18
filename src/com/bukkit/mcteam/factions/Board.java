package com.bukkit.mcteam.factions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

import com.bukkit.mcteam.factions.entities.EM;
import com.bukkit.mcteam.factions.util.TextUtil;
import com.bukkit.mcteam.gson.reflect.TypeToken;
import com.bukkit.mcteam.util.AsciiCompass;
import com.bukkit.mcteam.util.DiscUtil;

//import com.bukkit.mcteam.factions.util.*;

public class Board {
	protected static transient Map<String, Board> instances = new HashMap<String, Board>();
	protected static transient File file = new File(Factions.instance.getDataFolder(), "boards.json");
	
	public transient String worldName;
	protected Map<Coord, Integer> coordFactionIds = new HashMap<Coord, Integer>();
	
	public Board() {

	}
	
	public Board(String worldName) {
		this.worldName = worldName;
	}
	
	public Faction getFactionAt(Coord coord) {
		return Faction.get(getFactionIdAt(coord));
	}
	public int getFactionIdAt(Coord coord) {
		Integer factionId = coordFactionIds.get(coord);
		if (factionId == null) {
			return 0; // No faction
		}
		return factionId;
	}
	
	public void unclaim(Coord coord) {
		coordFactionIds.remove(coord);
		save();
	}
	
	public void claim(Coord coord, Faction faction) {
		coordFactionIds.put(coord, faction.id);
		save();
	}
	
	
	// Is this coord NOT completely surrounded by coords claimed by the same faction?
	// Simpler: Is there any nearby coord with a faction other than the faction here?
	public boolean isBorderCoord(Coord coord) {
		Faction faction = getFactionAt(coord);
		Coord a = coord.getRelative(1, 0);
		Coord b = coord.getRelative(-1, 0);
		Coord c = coord.getRelative(0, 1);
		Coord d = coord.getRelative(0, -1);
		return faction != this.getFactionAt(a) || faction != this.getFactionAt(b) || faction != this.getFactionAt(c) || faction != this.getFactionAt(d); 
	}
	
	//----------------------------------------------//
	// Clean boards
	//----------------------------------------------//
	
	// These functions search boards for orphaned foreign keys
	
	public void clean() {
		Iterator<Entry<Coord, Integer>> iter = coordFactionIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Coord, Integer> entry = iter.next();
			if ( ! EM.factionExists(entry.getValue())) {
				Factions.log("Cleaner removed coord with non existing factionId "+entry.getValue());
				iter.remove();
			}
		}
	}
	
	public static void cleanAll() {
		for (Board board : getAll()) {
			Factions.log("Cleaning board for world "+board.worldName);
			board.clean();
		}
	}	
	
	//----------------------------------------------//
	// Coord count
	//----------------------------------------------//
	
	public int getFactionCoordCount(int factionId) {
		int ret = 0;
		for (int thatFactionId : coordFactionIds.values()) {
			if(thatFactionId == factionId) {
				ret += 1;
			}
		}
		return ret;
	}
	public int getFactionCoordCount(Faction faction) {
		return getFactionCoordCount(faction.id);
	}
	
	public static int getFactionCoordCountAllBoards(int factionId) {
		int ret = 0;
		for (Board board : getAll()) {
			ret += board.getFactionCoordCount(factionId);
		}
		return ret;
	}
	public static int getFactionCoordCountAllBoards(Faction faction) {
		return getFactionCoordCountAllBoards(faction.id);
	}
	
	//----------------------------------------------//
	// Map generation
	//----------------------------------------------//
	
	/**
	 * The map is relative to a coord and a faction
	 * north is in the direction of decreasing x
	 * east is in the direction of decreasing z
	 */
	public ArrayList<String> getMap(Faction faction, Coord coord, double inDegrees) {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add(TextUtil.titleize("("+coord+") "+this.getFactionAt(coord).getTag(faction)));
		
		int halfWidth = Conf.mapWidth / 2;
		int halfHeight = Conf.mapHeight / 2;
		Coord topLeft = coord.getRelative(-halfHeight, halfWidth);
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
					Coord coordHere = topLeft.getRelative(dx, dz);
					Faction factionHere = this.getFactionAt(coordHere);
					if (factionHere.id == 0) {
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
	
	
	//----------------------------------------------//
	// Persistance
	//----------------------------------------------//
	
	/*public boolean save() {
		return EM.boardSave(this.worldName);
	}
	
	public static Board get(World world) {
		return EM.boardGet(world);
	}
	
	public static Collection<Board> getAll() {
		return EM.boardGetAll();
	}*/
	
	
	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	
	public boolean shouldBeSaved() {
		return this.coordFactionIds.size() > 0;
	}
	
	public static boolean save() {
		Factions.log("Saving boards to disk");
		
		Map<String, Board> instancesToSave = new HashMap<String, Board>();
		for (Entry<String, Board> entry : instances.entrySet()) {
			if (entry.getValue().shouldBeSaved()) {
				instancesToSave.put(entry.getKey(), entry.getValue());
			}
		}
		
		try {
			DiscUtil.write(file, Factions.gson.toJson(instancesToSave));
		} catch (IOException e) {
			Factions.log("Failed to save the boards to disk.");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean load() {
		if ( ! file.exists()) {
			Factions.log("No boards to load from disk. Creating new file.");
			save();
			return true;
		}
		
		try {
			Type type = new TypeToken<Map<String, Board>>(){}.getType();
			instances = Factions.gson.fromJson(DiscUtil.read(file), type);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		fillNames();
			
		return true;
	}
	
	public static void fillNames() {
		for(Entry<String, Board> entry : instances.entrySet()) {
			entry.getValue().worldName = entry.getKey();
		}
	}
	
	public static Board get(String worldName) {
		if (instances.containsKey(worldName)) {
			return instances.get(worldName);
		}
		
		Board board = new Board(worldName);
		instances.put(worldName, board);
		return board;
	}
	
	// You should use this one to be sure you do not spell the player name wrong.
	public static Board get(Board board) {
		return get(board.worldName);
	}
	
	public static Collection<Board> getAll() {
		return instances.values();
	}
}



















