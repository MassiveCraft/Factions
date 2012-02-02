package com.massivecraft.factions;

import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.CreatureType;

import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Rel;

public class Conf
{
	public static List<String> baseCommandAliases = new ArrayList<String>();
	public static boolean allowNoSlashCommand = true;
	
	// Colors
	public static ChatColor colorMember = ChatColor.GREEN;
	public static ChatColor colorAlly = ChatColor.DARK_PURPLE;
	public static ChatColor colorTruce = ChatColor.LIGHT_PURPLE;
	public static ChatColor colorNeutral = ChatColor.WHITE;
	public static ChatColor colorEnemy = ChatColor.RED;
	
	public static ChatColor colorNoPVP = ChatColor.GOLD;
	public static ChatColor colorFriendlyFire = ChatColor.DARK_RED;
	//public static ChatColor colorWilderness = ChatColor.DARK_GREEN;
	
	public static Map<FFlag, Boolean> factionFlagDefaults;
	public static Map<FFlag, Boolean> factionFlagIsChangeable;
	public static Map<FPerm, Set<Rel>> factionPermDefaults;
	
	// Power
	public static double powerPlayerMax = 10.0;
	public static double powerPlayerMin = -10.0;
	public static double powerPlayerStarting = 10.0; // New players start out with this power level
	public static double powerPerMinute = 0.2; // Default health rate... it takes 5 min to heal one power
	public static double powerPerDeath = 4.0; // A death makes you lose 4 power
	public static boolean scaleNegativePower = false; // Power regeneration rate increase as power decreases
	public static double scaleNegativeDivisor = 40.0; // Divisor for inverse power regeneration curve    
	public static boolean powerRegenOffline = false;  // does player power regenerate even while they're offline?
	public static double powerOfflineLossPerDay = 0.0;  // players will lose this much power per day offline
	public static double powerOfflineLossLimit = 0.0;  // players will no longer lose power from being offline once their power drops to this amount or less
	public static double powerFactionMax = 0.0;  // if greater than 0, the cap on how much power a faction can have (additional power from players beyond that will act as a "buffer" of sorts)
	
	public static String prefixLeader = "**";
	public static String prefixOfficer = "*";
	
	public static int factionTagLengthMin = 3;
	public static int factionTagLengthMax = 10;
	public static boolean factionTagForceUpperCase = false;
	
	public static boolean newFactionsDefaultOpen = false;

	// what faction ID to start new players in when they first join the server; default is 0, "no faction"
	public static String newPlayerStartingFactionID = "0";

	//public static boolean showMapFactionKey = true;
	//public static boolean showNeutralFactionsOnMap = true;
	//public static boolean showEnemyFactionsOnMap = true;
	
	// Disallow joining/leaving/kicking while power is negative
	public static boolean canLeaveWithNegativePower = true;
	
	// Configuration for faction-only chat
	public static boolean factionOnlyChat = true;
	// Configuration on the Faction tag in chat messages.
	public static boolean chatTagEnabled = true;
	public static transient boolean chatTagHandledByAnotherPlugin = false;
	public static boolean chatTagRelationColored = true;
	public static String chatTagReplaceString = "[FACTION]";
	public static String chatTagInsertAfterString = "";
	public static String chatTagInsertBeforeString = "";
	public static int chatTagInsertIndex = 1;
	public static boolean chatTagPadBefore = false;
	public static boolean chatTagPadAfter = true;
	public static String chatTagFormat = "%s"+ChatColor.WHITE;
	public static String factionChatFormat = "%s:"+ChatColor.WHITE+" %s";
	public static String allianceChatFormat = ChatColor.LIGHT_PURPLE+"%s:"+ChatColor.WHITE+" %s";
	
	public static double autoLeaveAfterDaysOfInactivity = 10.0;
	public static boolean removePlayerDataWhenBanned = true;

	public static boolean worldGuardChecking = false;

	//LWC
	public static boolean lwcIntegration = false;
	public static boolean onUnclaimResetLwcLocks = false;
	public static boolean onCaptureResetLwcLocks = false;

	// server logging options
	public static boolean logFactionCreate = true;
	public static boolean logFactionDisband = true;
	public static boolean logFactionJoin = true;
	public static boolean logFactionKick = true;
	public static boolean logFactionLeave = true;
	public static boolean logLandClaims = true;
	public static boolean logLandUnclaims = true;
	public static boolean logMoneyTransactions = true;
	
	public static boolean homesEnabled = true;
	public static boolean homesMustBeInClaimedTerritory = true;
	public static boolean homesTeleportToOnDeath = true;
	public static boolean homesRespawnFromNoPowerLossWorlds = true;
	public static boolean homesTeleportCommandEnabled = true;
	public static boolean homesTeleportCommandEssentialsIntegration = true;
	public static boolean homesTeleportCommandSmokeEffectEnabled = true;
	public static boolean homesTeleportAllowedFromEnemyTerritory = true;
	public static boolean homesTeleportAllowedFromDifferentWorld = true;
	public static double homesTeleportAllowedEnemyDistance = 32.0;
	public static boolean homesTeleportIgnoreEnemiesIfInOwnTerritory = true;
	
	
	public static Rel friendlyFireFromRel = Rel.TRUCE;
	public static boolean disablePVPForFactionlessPlayers = false;
	public static boolean enablePVPAgainstFactionlessInAttackersLand = false;
	
	public static int noPVPDamageToOthersForXSecondsAfterLogin = 3;

	//public static boolean peacefulMembersDisablePowerLoss = true;
	
	public static boolean permanentFactionsDisableLeaderPromotion = false;
	
	public static boolean claimsMustBeConnected = false;
	public static boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = true;
	public static int claimsRequireMinFactionMembers = 1;
	public static int claimedLandsMax = 0;
	
	//public static double considerFactionsReallyOfflineAfterXMinutes = 0.0;
	
	public static int actionDeniedPainAmount = 2;

	// commands which will be prevented if the player is a member of a permanent faction
	public static Set<String> permanentFactionMemberDenyCommands = new HashSet<String>();

	// commands which will be prevented when in claimed territory of another faction
	public static Set<String> territoryNeutralDenyCommands = new HashSet<String>();
	public static Set<String> territoryEnemyDenyCommands = new HashSet<String>();
	
	public static double territoryShieldFactor = 0.3;

	// for claimed areas where further faction-member ownership can be defined

	public static boolean pistonProtectionThroughDenyBuild = true;

	public final transient static Set<Material> materialsEditOnInteract = EnumSet.noneOf(Material.class);
	public final transient static Set<Material> materialsEditTools = EnumSet.noneOf(Material.class);
	public final transient static Set<Material> materialsDoor = EnumSet.noneOf(Material.class);
	public final transient static Set<Material> materialsContainer = EnumSet.noneOf(Material.class);
	
	//public static Set<Material> territoryProtectedMaterialsWhenOffline = EnumSet.noneOf(Material.class);
	//public static Set<Material> territoryDenyUseageMaterialsWhenOffline = EnumSet.noneOf(Material.class);
	
	// TODO: Rename to monsterCreatureTypes
	public static transient Set<CreatureType> monsters = EnumSet.noneOf(CreatureType.class);

	// Spout features
	public static boolean spoutFactionTagsOverNames = true;  // show faction tags over names over player heads
	public static boolean spoutFactionTitlesOverNames = true;  // whether to include player's title in that
	public static boolean spoutFactionLeaderCapes = true;  // Show capes on faction admins, colored based on the viewer's relation to the target player
	public static boolean spoutFactionOfficerCapes = true;  // same, but for faction moderators
	public static int spoutTerritoryDisplayPosition = 3;  // permanent territory display, instead of by chat; 0 = disabled, 1 = top left, 2 = top center, 3+ = top right
	public static float spoutTerritoryDisplaySize = 1.0f;  // text scale (size) for territory display
	public static boolean spoutTerritoryDisplayShowDescription = true;  // whether to show the faction description, not just the faction tag
	public static boolean spoutTerritoryOwnersShow = true;  // show territory owner list as well
	public static boolean spoutTerritoryNoticeShow = true;  // show additional brief territory notice near center of screen, to be sure player notices transition
	public static int spoutTerritoryNoticeTop = 40;  // how far down the screen to place the additional notice
	public static boolean spoutTerritoryNoticeShowDescription = false;  // whether to show the faction description in the notice, not just the faction tag
	public static float spoutTerritoryNoticeSize = 1.5f;  // text scale (size) for notice
	public static float spoutTerritoryNoticeLeaveAfterSeconds = 2.00f;  // how many seconds before the notice goes away
	public static String capeAlly = "https://github.com/MassiveCraft/Factions/raw/master/capes/ally.png";
	public static String capeEnemy = "https://github.com/MassiveCraft/Factions/raw/master/capes/enemy.png";
	public static String capeMember = "https://github.com/MassiveCraft/Factions/raw/master/capes/member.png";
	public static String capeNeutral = "https://github.com/MassiveCraft/Factions/raw/master/capes/neutral.png";
	public static String capePeaceful = "https://github.com/MassiveCraft/Factions/raw/master/capes/peaceful.png";
	
	// Economy settings
	public static boolean econEnabled = false;
	public static String econUniverseAccount = "";
	public static double econCostClaimWilderness = 30.0;
	public static double econCostClaimFromFactionBonus = 30.0;
	public static double econClaimAdditionalMultiplier = 0.5;
	public static double econClaimRefundMultiplier = 0.7;
	public static double econClaimUnconnectedFee = 0.0;
	public static double econCostCreate = 100.0;
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
	public static double econCostTruce = 0.0;
	public static double econCostNeutral = 0.0;
	public static double econCostEnemy = 0.0;
	
	//Faction banks, to pay for land claiming and other costs instead of individuals paying for them
	public static boolean bankEnabled = true;
	//public static boolean bankMembersCanWithdraw = false; //Have to be at least moderator to withdraw or pay money to another faction
	public static boolean bankFactionPaysCosts = true; //The faction pays for faction command costs, such as sethome
	public static boolean bankFactionPaysLandCosts = true; //The faction pays for land claiming costs.
	
	public static Set<String> worldsNoClaiming = new HashSet<String>();
	public static Set<String> worldsNoPowerLoss = new HashSet<String>();
	public static Set<String> worldsIgnorePvP = new HashSet<String>();
	// TODO: A better solution Would be to have One wilderness faction per world.
	//public static Set<String> worldsNoWildernessProtection = new HashSet<String>();
	
	public static transient int mapHeight = 8;
	public static transient int mapWidth = 39;
	public static transient char[] mapKeyChrs = "\\/#?$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz".toCharArray();
	
	static
	{
		baseCommandAliases.add("f");
		
		factionFlagDefaults = new LinkedHashMap<FFlag, Boolean>();
		for (FFlag flag : FFlag.values())
		{
			factionFlagDefaults.put(flag, flag.defaultDefaultValue);
		}
		
		factionPermDefaults = new LinkedHashMap<FPerm, Set<Rel>>();
		for (FPerm perm: FPerm.values())
		{
			factionPermDefaults.put(perm, perm.defaultDefaultValue);
		}
		
		territoryEnemyDenyCommands.add("home");
		territoryEnemyDenyCommands.add("sethome");
		territoryEnemyDenyCommands.add("spawn");
		territoryEnemyDenyCommands.add("tpahere");
		territoryEnemyDenyCommands.add("tpaccept");
		territoryEnemyDenyCommands.add("tpa");
		territoryEnemyDenyCommands.add("warp");

		materialsContainer.add(Material.DISPENSER);
		materialsContainer.add(Material.CHEST);
		materialsContainer.add(Material.FURNACE);
		materialsContainer.add(Material.BURNING_FURNACE);
		materialsContainer.add(Material.JUKEBOX);
		materialsContainer.add(Material.BREWING_STAND);
		materialsContainer.add(Material.ENCHANTMENT_TABLE);
		
		materialsEditOnInteract.add(Material.DIODE_BLOCK_OFF);
		materialsEditOnInteract.add(Material.DIODE_BLOCK_ON);
		materialsEditOnInteract.add(Material.NOTE_BLOCK);
		materialsEditOnInteract.add(Material.CAULDRON);
		materialsEditOnInteract.add(Material.SOIL);

		materialsDoor.add(Material.WOODEN_DOOR);
		materialsDoor.add(Material.TRAP_DOOR);
		materialsDoor.add(Material.FENCE_GATE);
		
		materialsEditTools.add(Material.FLINT_AND_STEEL);
		materialsEditTools.add(Material.BUCKET);
		materialsEditTools.add(Material.WATER_BUCKET);
		materialsEditTools.add(Material.LAVA_BUCKET);

		monsters.add(CreatureType.BLAZE);
		monsters.add(CreatureType.CAVE_SPIDER);
		monsters.add(CreatureType.CREEPER);
		monsters.add(CreatureType.ENDERMAN);
		monsters.add(CreatureType.ENDER_DRAGON);
		monsters.add(CreatureType.GHAST);
		monsters.add(CreatureType.GIANT);
		monsters.add(CreatureType.MAGMA_CUBE);
		monsters.add(CreatureType.PIG_ZOMBIE);
		monsters.add(CreatureType.SILVERFISH);
		monsters.add(CreatureType.SKELETON);
		monsters.add(CreatureType.SLIME);
		monsters.add(CreatureType.SPIDER);
		monsters.add(CreatureType.ZOMBIE);
	}

	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	private static transient Conf i = new Conf();
	public static void load()
	{
		P.p.persist.loadOrSaveDefault(i, Conf.class, "conf");
	}
	public static void save()
	{
		P.p.persist.save(i);
	}
}

