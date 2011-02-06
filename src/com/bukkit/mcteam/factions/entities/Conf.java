package com.bukkit.mcteam.factions.entities;

import java.util.*;

import org.bukkit.*;

import com.bukkit.mcteam.factions.struct.Relation;

public class Conf {
	public static Integer logThreshold;
	public static String prefixAdmin;
	public static String prefixMod;
	public static int factionNameMinLength;
	public static int factionNameMaxLength;
	
	public static int mapHeight;
	public static int mapWidth;
	
	public static double territoryShieldFactor;
	
	// Chat control:
	public static boolean useRelationColoredChat; // This can interfere with other chat formatting plugins. Test to turn it on or off.
	// TODO experiment with displayname feature of bukkit
	// TODO test to set format instead of overriding and offer a non colored mut **Title alternative...
	
	// Colors
	public static ChatColor colorMember;
	public static ChatColor colorAlly;
	public static ChatColor colorNeutral;
	public static ChatColor colorEnemy;
	
	public static ChatColor colorSystem;
	public static ChatColor colorAction;
	public static ChatColor colorChrome;
	public static ChatColor colorCommand;
	public static ChatColor colorParameter;
	
	// Command names / aliases	
	public static List<String> aliasBase = new ArrayList<String>();
	public static List<String> aliasHelp = new ArrayList<String>();
	
	public static List<String> aliasList = new ArrayList<String>();
	public static List<String> aliasShow = new ArrayList<String>();
	
	public static List<String> aliasMap = new ArrayList<String>();
	public static List<String> aliasHere = new ArrayList<String>();
	
	
	public static List<String> aliasJoin = new ArrayList<String>();
	public static List<String> aliasLeave = new ArrayList<String>();
	
	public static List<String> aliasCreate = new ArrayList<String>();
	public static List<String> aliasName = new ArrayList<String>();
	
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
	
	public static List<String> aliasDescription = new ArrayList<String>();
	
	public static List<String> aliasVersion = new ArrayList<String>();
	
	// Value aliases
	public static List<String> aliasTrue = new ArrayList<String>();
	
	// Power
	public static double powerPerLand;
	public static double powerPerPlayer;
	public static double powerPerMinute; // Default health rate
	public static double powerPerDeath;
	public static double powerDefaultBonus;
	
	// Protected blocks
	public static List<Material> territoryProtectedMaterials = new ArrayList<Material>();
	
	static {
		logThreshold = 10;
		prefixAdmin = "**";
		prefixMod = "*";
		factionNameMinLength = 3;
		factionNameMaxLength = 40;
		
		mapHeight = 8;
		mapWidth = 49;
		
		territoryShieldFactor = 0.33;

		useRelationColoredChat = true;
		
		colorMember = ChatColor.GREEN;
		colorAlly = ChatColor.LIGHT_PURPLE;
		colorNeutral = ChatColor.WHITE;
		colorEnemy = ChatColor.RED;
		
		colorSystem = ChatColor.YELLOW;
		colorAction = ChatColor.LIGHT_PURPLE;
		colorChrome = ChatColor.GOLD;
		colorCommand = ChatColor.AQUA;
		colorParameter = ChatColor.DARK_AQUA;
		
		aliasBase.add("/f");
		aliasBase.add("f");
		aliasBase.add("/faction");
		aliasBase.add("faction");
		aliasBase.add("/factions");
		aliasBase.add("factions");
		
		aliasHelp.add("help");
		aliasHelp.add("h");
		aliasHelp.add("?");
		
		aliasList.add("list");
		aliasList.add("ls");
		
		aliasShow.add("show");
		aliasShow.add("who");
		
		aliasMap.add("map");
		aliasHere.add("here");
		
		aliasJoin.add("join");
		
		aliasLeave.add("leave");
		
		aliasCreate.add("create");
		aliasCreate.add("new");
		
		aliasName.add("name");
		aliasName.add("rename");
		
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
		
		aliasDescription.add("desc");
		
		aliasVersion.add("version");
		
		aliasTrue.add("true");
		aliasTrue.add("yes");
		aliasTrue.add("y");
		aliasTrue.add("ok");
		aliasTrue.add("on");
		aliasTrue.add("+");
		
		powerPerLand = 1; // 1 power grants one land
		powerPerPlayer = 5; // One player has 5 power
		powerPerMinute = 0.2; // Default health rate... it takes 5 min to heal one death
		powerPerDeath = 1; //A death makes you loose 2 power
		powerDefaultBonus = 0; //A faction normally has a power bonus
		
		territoryProtectedMaterials.add(Material.WOODEN_DOOR);
		territoryProtectedMaterials.add(Material.DISPENSER);
		territoryProtectedMaterials.add(Material.CHEST);
		territoryProtectedMaterials.add(Material.FURNACE);
	}
	
	//----------------------------------------------//
	// Color picking and stuff
	//----------------------------------------------//
	
	public static ChatColor relationColor(Relation relation) {
		if (relation == Relation.MEMBER) {
			return colorMember;
		} else if (relation == Relation.ALLY) {
			return colorAlly;
		} else if (relation == Relation.NEUTRAL) {
			return colorNeutral;
		} else { //if (relation == FactionRelation.ENEMY) {
			return colorEnemy;
		}
	}
	
	//----------------------------------------------//
	// Persistance
	//----------------------------------------------//
	
	public static boolean save() {
		return EM.configSave();
	}
}
