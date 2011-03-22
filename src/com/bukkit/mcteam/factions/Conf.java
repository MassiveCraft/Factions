package com.bukkit.mcteam.factions;

import java.io.File;
import java.io.IOException;
import java.util.*;
import org.bukkit.*;

import com.bukkit.mcteam.util.DiscUtil;

public class Conf {
	public static transient File file = new File(Factions.instance.getDataFolder(), "conf.json");
	
	// Colors
	public static ChatColor colorMember = ChatColor.GREEN;
	public static ChatColor colorAlly = ChatColor.LIGHT_PURPLE;
	public static ChatColor colorNeutral = ChatColor.WHITE;
	public static ChatColor colorEnemy = ChatColor.RED;
	
	public static ChatColor colorSystem = ChatColor.YELLOW;
	public static ChatColor colorChrome = ChatColor.GOLD;
	public static ChatColor colorCommand = ChatColor.AQUA;
	public static ChatColor colorParameter = ChatColor.DARK_AQUA;
	
	// Power
	public static double powerPlayerMax = 10;
	public static double powerPlayerMin = -10;
	public static double powerPerMinute = 0.2; // Default health rate... it takes 5 min to heal one power
	public static double powerPerDeath = 2; //A death makes you loose 2 power
	
	public static String prefixAdmin = "**";
	public static String prefixMod = "*";
	
	public static int factionTagLengthMin = 3;
	public static int factionTagLengthMax = 10;
	public static boolean factionTagForceUpperCase = false;
	
	// Configuration on the Faction tag in chat messages.
	
	public static boolean chatTagEnabled = true;
	public static boolean chatTagRelationColored = true;
	public static int chatTagInsertIndex = 1;
	public static String chatTagFormat = "%s"+ChatColor.WHITE+" ";
	public static String factionChatFormat = "%s"+ChatColor.WHITE+" %s";
	
	public static int mapHeight = 8;
	public static int mapWidth = 49;

	public static double territoryShieldFactor = 0.5;
	public static boolean territoryBlockCreepers = false;
	public static boolean territoryBlockFireballs = false;
	
	public static List<Material> territoryProtectedMaterials = new ArrayList<Material>();

	public static boolean allowNoSlashCommand = true;
	
	public static double autoLeaveFactionAfterDaysOfInactivity = 14;
	
	static {
		territoryProtectedMaterials.add(Material.WOODEN_DOOR);
		territoryProtectedMaterials.add(Material.DISPENSER);
		territoryProtectedMaterials.add(Material.CHEST);
		territoryProtectedMaterials.add(Material.FURNACE);
	}
	
	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	
	public static boolean save() {
		Factions.log("Saving config to disk.");
		try {
			DiscUtil.write(file, Factions.gson.toJson(new Conf()));
		} catch (IOException e) {
			e.printStackTrace();
			Factions.log("Failed to save the config to disk.");
			return false;
		}
		return true;
	}
	
	public static boolean load() {
		if ( ! file.exists()) {
			Factions.log("No conf to load from disk. Creating new file.");
			save();
			return true;
		}
		
		try {
			Factions.gson.fromJson(DiscUtil.read(file), Conf.class);
		} catch (IOException e) {
			e.printStackTrace();
			Factions.log("Failed to load the config from disk.");
			return false;
		}
		
		return true;
	}
}
