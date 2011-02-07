package com.bukkit.mcteam.factions.entities;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

import com.bukkit.mcteam.factions.util.TextUtil;
import com.bukkit.mcteam.util.AsciiCompass;

//import com.bukkit.mcteam.factions.util.*;

public class Board {
	protected static Map<Coord, Integer> coordFactionIds;
	
	static {
		coordFactionIds = new HashMap<Coord, Integer>();
	}
	
	public static Faction getFactionAt(Coord coord) {
		return Faction.get(getFactionIdAt(coord));
	}
	public static int getFactionIdAt(Coord coord) {
		Integer factionId = coordFactionIds.get(coord);
		if (factionId == null) {
			return 0; // No faction
		}
		return factionId;
	}
	
	public static void unclaim(Coord coord) {
		coordFactionIds.remove(coord);
		save();
	}
	
	public static void claim(Coord coord, Faction faction) {
		coordFactionIds.put(coord, faction.id);
		save();
	}
	
	public static boolean isBorderCoord(Coord coord) {
		Faction faction = Board.getFactionAt(coord);
		Coord a = coord.getRelative(1, 0);
		Coord b = coord.getRelative(-1, 0);
		Coord c = coord.getRelative(0, 1);
		Coord d = coord.getRelative(0, -1);
		return faction != a.getFaction() && faction != b.getFaction() && faction != c.getFaction() && faction != d.getFaction(); 
	}
	
	public static void purgeFaction(Faction faction) {
		purgeFaction(faction.id);
	}
	public static void purgeFaction(int factionId) {
		Iterator<Entry<Coord, Integer>> iter = coordFactionIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Coord, Integer> entry = iter.next();
			if (entry.getValue().equals(factionId)) {
				iter.remove();
			}
		}
	}
	
	public static int getFactionCoordCount(Faction faction) {
		return getFactionCoordCount(faction.id);
	}
	public static int getFactionCoordCount(int factionId) {
		int ret = 0;
		for (int thatFactionId : coordFactionIds.values()) {
			if(thatFactionId == factionId) {
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
	public static ArrayList<String> getMap(Faction faction, Coord coord, double inDegrees) {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add(TextUtil.titleize("("+coord+") "+coord.getFaction().getName(faction)));
		
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
					Faction factionHere = coordHere.getFaction();
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
	
	public static boolean save() {
		return EM.boardSave();
	}
}



















