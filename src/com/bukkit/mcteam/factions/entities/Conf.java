package com.bukkit.mcteam.factions.entities;

import java.util.*;
import org.bukkit.*;

public class Conf {
	// Colors
	public static ChatColor colorMember = ChatColor.GREEN;
	public static ChatColor colorAlly = ChatColor.LIGHT_PURPLE;
	public static ChatColor colorNeutral = ChatColor.WHITE;
	public static ChatColor colorEnemy = ChatColor.RED;
	
	public static ChatColor colorSystem = ChatColor.YELLOW;
	public static ChatColor colorChrome = ChatColor.GOLD;
	public static ChatColor colorCommand = ChatColor.AQUA;
	public static ChatColor colorParameter = ChatColor.DARK_AQUA;

	public static Integer logThreshold = 10;
	
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
	public static List<Material> territoryProtectedMaterials = new ArrayList<Material>();
	
	// Command names / aliases	
	public static List<String> aliasBase = new ArrayList<String>();
	public static List<String> aliasHelp = new ArrayList<String>();
	
	public static List<String> aliasList = new ArrayList<String>();
	public static List<String> aliasShow = new ArrayList<String>();
	
	public static List<String> aliasMap = new ArrayList<String>();
	
	public static List<String> aliasJoin = new ArrayList<String>();
	public static List<String> aliasLeave = new ArrayList<String>();
	
	public static List<String> aliasCreate = new ArrayList<String>();
	public static List<String> aliasTag = new ArrayList<String>();
	public static List<String> aliasDescription = new ArrayList<String>();
	public static List<String> aliasChat = new ArrayList<String>();
	
	public static List<String> aliasTitle = new ArrayList<String>();
	public static List<String> aliasInvite = new ArrayList<String>();
	public static List<String> aliasDeinvite = new ArrayList<String>();
	public static List<String> aliasOpen = new ArrayList<String>();
	
	public static List<String> aliasKick = new ArrayList<String>();
	public static List<String> aliasModerator = new ArrayList<String>();
	public static List<String> aliasAdmin = new ArrayList<String>();
	
	public static List<String> aliasClaim = new ArrayList<String>();
	public static List<String> aliasUnclaim = new ArrayList<String>();
	
	public static List<String> aliasRelationAlly = new ArrayList<String>();
	public static List<String> aliasRelationNeutral = new ArrayList<String>();
	public static List<String> aliasRelationEnemy = new ArrayList<String>();
	
	public static List<String> aliasVersion = new ArrayList<String>();
	
	// Value aliases
	public static List<String> aliasTrue = new ArrayList<String>();
	
	static {
		territoryProtectedMaterials.add(Material.WOODEN_DOOR);
		territoryProtectedMaterials.add(Material.DISPENSER);
		territoryProtectedMaterials.add(Material.CHEST);
		territoryProtectedMaterials.add(Material.FURNACE);
		
		aliasBase.add("/f");
		aliasBase.add("f");
		
		aliasHelp.add("help");
		aliasHelp.add("h");
		aliasHelp.add("?");
		
		aliasList.add("list");
		aliasList.add("ls");
		
		aliasShow.add("show");
		aliasShow.add("who");
		
		aliasMap.add("map");
		
		aliasJoin.add("join");
		
		aliasLeave.add("leave");
		
		aliasCreate.add("create");
		aliasCreate.add("new");
		aliasTag.add("tag");
		aliasDescription.add("desc");
		
		aliasChat.add("chat");
		aliasChat.add("c");
		
		aliasTitle.add("title");
		
		aliasInvite.add("invite");
		aliasInvite.add("inv");
		
		aliasDeinvite.add("deinvite");
		aliasDeinvite.add("deinv");
		
		aliasOpen.add("open");
		aliasOpen.add("close");
		
		aliasKick.add("kick");
		
		aliasModerator.add("mod");
		
		aliasAdmin.add("admin");
		
		aliasClaim.add("claim");
		
		aliasUnclaim.add("unclaim");
		aliasUnclaim.add("declaim");
		
		aliasRelationAlly.add("ally");
		aliasRelationNeutral.add("neutral");
		aliasRelationEnemy.add("enemy");
		
		aliasVersion.add("version");
		
		aliasTrue.add("true");
		aliasTrue.add("yes");
		aliasTrue.add("y");
		aliasTrue.add("ok");
		aliasTrue.add("on");
		aliasTrue.add("+");
	}
	
	//----------------------------------------------//
	// Persistance
	//----------------------------------------------//
	
	public static boolean save() {
		return EM.configSave();
	}
}
