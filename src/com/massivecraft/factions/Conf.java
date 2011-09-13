package com.massivecraft.factions;

import java.io.File;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.CreatureType;

import com.massivecraft.factions.util.DiscUtil;


public class Conf {
	public static final transient File file = new File(Factions.instance.getDataFolder(), "conf.json");
	
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
	public static double powerPlayerMax = 10.0;
	public static double powerPlayerMin = -10.0;
	public static double powerPerMinute = 0.2; // Default health rate... it takes 5 min to heal one power
	public static double powerPerDeath = 4.0; // A death makes you lose 4 power
	public static boolean powerRegenOffline = false;  // does player power regenerate even while they're offline?
	public static double powerFactionMax = 0.0;  // if greater than 0, the cap on how much power a faction can have (additional power from players beyond that will act as a "buffer" of sorts)
	
	public static String prefixAdmin = "**";
	public static String prefixMod = "*";
	
	public static int factionTagLengthMin = 3;
	public static int factionTagLengthMax = 10;
	public static boolean factionTagForceUpperCase = false;
	
	public static boolean newFactionsDefaultOpen = true;

	// what faction ID to start new players in when they first join the server; default is 0, "no faction"
	public static int newPlayerStartingFactionID = 0;

	public static boolean showMapFactionKey = true;
	public static boolean showNeutralFactionsOnMap = true;
	public static boolean showEnemyFactionsOnMap = true;
	
	// Disallow joining/leaving/kicking while power is negative
	public static boolean CanLeaveWithNegativePower = true;
	
	// Configuration for faction-only chat
	public static boolean factionOnlyChat = true;
	// Configuration on the Faction tag in chat messages.
	public static boolean chatTagEnabled = true;
	public static transient boolean chatTagHandledByAnotherPlugin = false;
	public static boolean chatTagRelationColored = true;
	public static String chatTagReplaceString = "{FACTION}";
	public static String chatTagInsertAfterString = "";
	public static String chatTagInsertBeforeString = "";
	public static int chatTagInsertIndex = 1;
	public static boolean chatTagPadBefore = false;
	public static boolean chatTagPadAfter = true;
	public static String chatTagFormat = "%s"+ChatColor.WHITE;
	public static String factionChatFormat = "%s"+ChatColor.WHITE+" %s";
	
	public static boolean allowNoSlashCommand = true;
	
	public static double autoLeaveAfterDaysOfInactivity = 14.0;
	
	public static boolean worldGuardChecking = false;
	
	public static boolean homesEnabled = true;
	public static boolean homesMustBeInClaimedTerritory = true;
	public static boolean homesTeleportToOnDeath = true;
	public static boolean homesRespawnFromNoPowerLossWorlds = true;
	public static boolean homesTeleportCommandEnabled = true;
	public static boolean homesTeleportAllowedFromEnemyTerritory = true;
	public static boolean homesTeleportAllowedFromDifferentWorld = true;
	public static double homesTeleportAllowedEnemyDistance = 32.0;
	public static boolean homesTeleportIgnoreEnemiesIfInOwnTerritory = true;
	
	public static boolean disablePVPBetweenNeutralFactions = false;
	public static boolean disablePVPForFactionlessPlayers = false;
	public static boolean enablePVPAgainstFactionlessInAttackersLand = false;
	
	public static int noPVPDamageToOthersForXSecondsAfterLogin = 3;

	public static boolean peacefulTerritoryDisablePVP = true;
	public static boolean peacefulTerritoryDisableMonsters = false;
	public static boolean peacefulMembersDisablePowerLoss = true;
	
	public static boolean claimsMustBeConnected = false;
	public static boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = true;
	public static int claimsRequireMinFactionMembers = 1;
	
	public static double considerFactionsReallyOfflineAfterXMinutes = 0.0;
	
	public static int actionDeniedPainAmount = 1;
	
	// commands which will be prevented when in claimed territory of another faction
	public static Set<String> territoryNeutralDenyCommands = new HashSet<String>();
	public static Set<String> territoryEnemyDenyCommands = new HashSet<String>();
	
	public static double territoryShieldFactor = 0.3;
	public static boolean territoryDenyBuild = true;
	public static boolean territoryDenyBuildWhenOffline = true;
	public static boolean territoryPainBuild = false;
	public static boolean territoryPainBuildWhenOffline = false;
	public static boolean territoryDenyUseage = true;
	public static boolean territoryEnemyDenyBuild = true;
	public static boolean territoryEnemyDenyBuildWhenOffline = true;
	public static boolean territoryEnemyPainBuild = false;
	public static boolean territoryEnemyPainBuildWhenOffline = false;
	public static boolean territoryEnemyDenyUseage = true;
	public static boolean territoryEnemyProtectMaterials = true;
	public static boolean territoryAllyDenyBuild = true;
	public static boolean territoryAllyDenyBuildWhenOffline = true;
	public static boolean territoryAllyPainBuild = false;
	public static boolean territoryAllyPainBuildWhenOffline = false;
	public static boolean territoryAllyDenyUseage = true;
	public static boolean territoryAllyProtectMaterials = true;
	public static boolean territoryBlockCreepers = false;
	public static boolean territoryBlockCreepersWhenOffline = false;
	public static boolean territoryBlockFireballs = false;
	public static boolean territoryBlockFireballsWhenOffline = false;
	public static boolean territoryBlockTNT = false;
	public static boolean territoryBlockTNTWhenOffline = false;

	public static boolean safeZoneDenyBuild = true;
	public static boolean safeZoneDenyUseage = true;
	public static boolean safeZoneBlockTNT = true;
	public static boolean safeZonePreventAllDamageToPlayers = false;
	
	public static boolean warZoneDenyBuild = true;
	public static boolean warZoneDenyUseage = true;
	public static boolean warZoneBlockCreepers = false;
	public static boolean warZoneBlockFireballs = false;
	public static boolean warZoneBlockTNT = true;
	public static boolean warZonePowerLoss = true;
	public static boolean warZoneFriendlyFire = false;
	
	public static boolean wildernessDenyBuild = false;
	public static boolean wildernessDenyUseage = false;
	public static boolean wildernessBlockCreepers = false;
	public static boolean wildernessBlockFireballs = false;
	public static boolean wildernessBlockTNT = false;
	public static boolean wildernessPowerLoss = true;

	// for claimed areas where further faction-member ownership can be defined
	public static boolean ownedAreasEnabled = true;
	public static int ownedAreasLimitPerFaction = 0;
	public static boolean ownedAreasModeratorsCanSet = false;
	public static boolean ownedAreaModeratorsBypass = true;
	public static boolean ownedAreaDenyBuild = true;
	public static boolean ownedAreaPainBuild = false;
	public static boolean ownedAreaProtectMaterials = true;
	public static boolean ownedAreaDenyUseage = true;

	public static String ownedLandMessage = "Owner(s): ";
	public static String publicLandMessage = "Public faction land.";
	public static boolean ownedMessageOnBorder = true;
	public static boolean ownedMessageInsideTerritory = true;
	public static boolean ownedMessageByChunk = false;

	public static boolean pistonProtectionThroughDenyBuild = true;

	public static Set<Material> territoryProtectedMaterials = EnumSet.noneOf(Material.class);
	public static Set<Material> territoryDenyUseageMaterials = EnumSet.noneOf(Material.class);
	public static Set<Material> territoryProtectedMaterialsWhenOffline = EnumSet.noneOf(Material.class);
	public static Set<Material> territoryDenyUseageMaterialsWhenOffline = EnumSet.noneOf(Material.class);
	
	public static transient Set<CreatureType> safeZoneNerfedCreatureTypes = EnumSet.noneOf(CreatureType.class);

	// Spout features
	public static boolean spoutFactionTagsOverNames = true;
	public static boolean spoutFactionTitlesOverNames = true;
	public static boolean spoutFactionAdminCapes = true;
	public static boolean spoutFactionModeratorCapes = true;
	public static String capeAlly = "https://github.com/MassiveCraft/Factions/raw/master/capes/ally.png";
	public static String capeEnemy = "https://github.com/MassiveCraft/Factions/raw/master/capes/enemy.png";
	public static String capeMember = "https://github.com/MassiveCraft/Factions/raw/master/capes/member.png";
	public static String capeNeutral = "https://github.com/MassiveCraft/Factions/raw/master/capes/neutral.png";
	public static String capePeaceful = "https://github.com/MassiveCraft/Factions/raw/master/capes/peaceful.png";
	
	// Economy settings
	public static boolean econIConomyEnabled = false;
	public static boolean econEssentialsEcoEnabled = false;
	public static double econCostClaimWilderness = 30.0;
	public static double econCostClaimFromFactionBonus = 30.0;
	public static double econClaimAdditionalMultiplier = 0.5;
	public static double econClaimRefundMultiplier = 0.7;
	public static double econCostCreate = 100.0;
	public static double econCostOwner = 15.0;
	public static double econCostSethome = 30.0;
	public static double econCostJoin = 0.0;
	public static double econCostLeave = 0.0;
	public static double econCostKick = 0.0;
	public static double econCostInvite = 0.0;
	public static double econCostHome = 0.0;
	public static double econCostTag = 0.0;
	public static double econCostDesc = 0.0;
	public static double econCostTitle = 0.0;
	public static double econCostList = 0.0;
	public static double econCostMap = 0.0;
	public static double econCostPower = 0.0;
	public static double econCostShow = 0.0;
	public static double econCostOpen = 0.0;
	public static double econCostAlly = 0.0;
	public static double econCostEnemy = 0.0;
	public static double econCostNeutral = 0.0;
	public static double econCostNoBoom = 0.0;
	
	public static Set<String> worldsNoClaiming = new HashSet<String>();
	public static Set<String> worldsNoPowerLoss = new HashSet<String>();
	public static Set<String> worldsIgnorePvP = new HashSet<String>();
	public static Set<String> worldsNoWildernessProtection = new HashSet<String>();
	
	public static transient int mapHeight = 8;
	public static transient int mapWidth = 39;
	public static transient char[] mapKeyChrs = "\\/#?$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz".toCharArray();
	
	static {
		territoryEnemyDenyCommands.add("home");
		territoryEnemyDenyCommands.add("sethome");
		territoryEnemyDenyCommands.add("spawn");

		territoryProtectedMaterials.add(Material.WOODEN_DOOR);
		territoryProtectedMaterials.add(Material.TRAP_DOOR);
		territoryProtectedMaterials.add(Material.DISPENSER);
		territoryProtectedMaterials.add(Material.CHEST);
		territoryProtectedMaterials.add(Material.FURNACE);
		territoryProtectedMaterials.add(Material.BURNING_FURNACE);
		territoryProtectedMaterials.add(Material.DIODE_BLOCK_OFF);
		territoryProtectedMaterials.add(Material.DIODE_BLOCK_ON);

		territoryDenyUseageMaterials.add(Material.FLINT_AND_STEEL);
		territoryDenyUseageMaterials.add(Material.BUCKET);
		territoryDenyUseageMaterials.add(Material.WATER_BUCKET);
		territoryDenyUseageMaterials.add(Material.LAVA_BUCKET);

		territoryProtectedMaterialsWhenOffline.add(Material.WOODEN_DOOR);
		territoryProtectedMaterialsWhenOffline.add(Material.TRAP_DOOR);
		territoryProtectedMaterialsWhenOffline.add(Material.DISPENSER);
		territoryProtectedMaterialsWhenOffline.add(Material.CHEST);
		territoryProtectedMaterialsWhenOffline.add(Material.FURNACE);
		territoryProtectedMaterialsWhenOffline.add(Material.BURNING_FURNACE);
		territoryProtectedMaterialsWhenOffline.add(Material.DIODE_BLOCK_OFF);
		territoryProtectedMaterialsWhenOffline.add(Material.DIODE_BLOCK_ON);

		territoryDenyUseageMaterialsWhenOffline.add(Material.FLINT_AND_STEEL);
		territoryDenyUseageMaterialsWhenOffline.add(Material.BUCKET);
		territoryDenyUseageMaterialsWhenOffline.add(Material.WATER_BUCKET);
		territoryDenyUseageMaterialsWhenOffline.add(Material.LAVA_BUCKET);

		safeZoneNerfedCreatureTypes.add(CreatureType.CREEPER);
		safeZoneNerfedCreatureTypes.add(CreatureType.GHAST);
		safeZoneNerfedCreatureTypes.add(CreatureType.PIG_ZOMBIE);
		safeZoneNerfedCreatureTypes.add(CreatureType.SKELETON);
		safeZoneNerfedCreatureTypes.add(CreatureType.SPIDER);
		safeZoneNerfedCreatureTypes.add(CreatureType.SLIME);
		safeZoneNerfedCreatureTypes.add(CreatureType.ZOMBIE);
	}

	// track players with admin access who have enabled "admin bypass" mode, and should therefore be able to build anywhere
	// not worth saving between server restarts, I think
	public static transient Set<String> adminBypassPlayers = Collections.synchronizedSet(new HashSet<String>());

	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	
	public static boolean save() {
		//Factions.log("Saving config to disk.");
		
		try {
			DiscUtil.write(file, Factions.instance.gson.toJson(new Conf()));
		} catch (Exception e) {
			e.printStackTrace();
			Factions.log("Failed to save the config to disk.");
			return false;
		}
		return true;
	}
	
	public static boolean load() {
		Factions.log("Loading conf from disk");
		
		if ( ! file.exists()) {
			Factions.log("No conf to load from disk. Creating new file.");
			save();
			return true;
		}
		
		try {
			Factions.instance.gson.fromJson(DiscUtil.read(file), Conf.class);
		} catch (Exception e) {
			e.printStackTrace();
			Factions.log("Failed to load the config from disk.");
			return false;
		}
		
		return true;
	}
}

