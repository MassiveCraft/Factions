package org.mcteam.factions;

import java.io.File;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.CreatureType;
import org.mcteam.factions.util.DiscUtil;


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
	public static double powerPerDeath = 4; // A death makes you lose 4 power
	public static boolean powerRegenOffline = false;  // does player power regenerate even while they're offline?
	
	public static String prefixAdmin = "**";
	public static String prefixMod = "*";
	
	public static int factionTagLengthMin = 3;
	public static int factionTagLengthMax = 10;
	public static boolean factionTagForceUpperCase = false;
	
	public static boolean newFactionsDefaultOpen = true;

	public static boolean showMapFactionKey = true;
	
	// Disallow joining/leaving/kicking while power is negative
	public static boolean CanLeaveWithNegativePower = true;
	
	// Configuration on the Faction tag in chat messages.
	public static boolean preloadChatPlugins = true;
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
	
	public static double autoLeaveAfterDaysOfInactivity = 14;
	
	public static boolean homesEnabled = true;
	public static boolean homesMustBeInClaimedTerritory = true;
	public static boolean homesTeleportToOnDeath = true;
	public static boolean homesTeleportAllowedFromEnemyTerritory = true;
	public static double homesTeleportAllowedEnemyDistance = 32;
	
	public static boolean disablePVPForFactionlessPlayers = false;
	public static int noPVPDamageToOthersForXSecondsAfterLogin = 3;
	
	public static boolean claimsMustBeConnected = false;
	
	public static double considerFactionsReallyOfflineAfterXMinutes = 0;
	
	public static double territoryShieldFactor = 0.3;
	public static boolean territoryDenyBuild = true;
	public static boolean territoryDenyBuildWhenOffline = true;
	public static boolean territoryDenyUseage = true;
	public static boolean territoryBlockCreepers = false;
	public static boolean territoryBlockCreepersWhenOffline = false;
	public static boolean territoryBlockFireballs = false;
	public static boolean territoryBlockFireballsWhenOffline = false;
	public static boolean territoryBlockTNT = false;
	public static boolean territoryBlockTNTWhenOffline = false;

	public static boolean safeZoneDenyBuild = true;
	public static boolean safeZoneDenyUseage = true;
	public static boolean safeZoneBlockTNT = true;
	
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
	
	public static Set<Material> territoryProtectedMaterials = EnumSet.noneOf(Material.class);
	public static Set<Material> territoryDenyUseageMaterials = EnumSet.noneOf(Material.class);
	public static Set<Material> territoryProtectedMaterialsWhenOffline = EnumSet.noneOf(Material.class);
	public static Set<Material> territoryDenyUseageMaterialsWhenOffline = EnumSet.noneOf(Material.class);
	
	public static transient Set<CreatureType> safeZoneNerfedCreatureTypes = EnumSet.noneOf(CreatureType.class);
	
	public static Set<String> worldsNoClaiming = new HashSet<String>();
	public static Set<String> worldsNoPowerLoss = new HashSet<String>();
	
	public static transient int mapHeight = 8;
	public static transient int mapWidth = 39;
	public static transient char[] mapKeyChrs = "\\/#?$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz".toCharArray();
	
	static {
		territoryProtectedMaterials.add(Material.WOODEN_DOOR);
		territoryProtectedMaterials.add(Material.TRAP_DOOR);
		territoryProtectedMaterials.add(Material.DISPENSER);
		territoryProtectedMaterials.add(Material.CHEST);
		territoryProtectedMaterials.add(Material.FURNACE);

		territoryDenyUseageMaterials.add(Material.FLINT_AND_STEEL);
		territoryDenyUseageMaterials.add(Material.BUCKET);
		territoryDenyUseageMaterials.add(Material.WATER_BUCKET);
		territoryDenyUseageMaterials.add(Material.LAVA_BUCKET);
		
		territoryProtectedMaterialsWhenOffline.add(Material.WOODEN_DOOR);
		territoryProtectedMaterialsWhenOffline.add(Material.TRAP_DOOR);
		territoryProtectedMaterialsWhenOffline.add(Material.DISPENSER);
		territoryProtectedMaterialsWhenOffline.add(Material.CHEST);
		territoryProtectedMaterialsWhenOffline.add(Material.FURNACE);

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
			DiscUtil.write(file, Factions.gson.toJson(new Conf()));
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
			Factions.gson.fromJson(DiscUtil.read(file), Conf.class);
		} catch (Exception e) {
			e.printStackTrace();
			Factions.log("Failed to load the config from disk.");
			return false;
		}
		
		return true;
	}
}

