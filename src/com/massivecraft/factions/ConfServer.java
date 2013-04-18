package com.massivecraft.factions;

import java.util.*;

import org.bukkit.*;

import com.massivecraft.mcore.SimpleConfig;
import com.massivecraft.mcore.util.MUtil;

public class ConfServer extends SimpleConfig
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static transient ConfServer i = new ConfServer();
	public static ConfServer get() { return i; }
	public ConfServer() { super(Factions.get()); }
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public static List<String> baseCommandAliases = MUtil.list("f");
	public static String dburi = "default";
	
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
	//public static Map<FFlag, Boolean> factionFlagIsChangeable;
	public static Map<FPerm, Set<Rel>> factionPermDefaults;
	
	// TODO: Shouldn't this be a constant rather?
	public static Rel factionRankDefault = Rel.RECRUIT;
	
	// Power
	public static double powerMax = 10.0;
	public static double powerMin = -10.0;
	public static double powerStarting = 10.0; // New players start out with this power level
	
	public static double powerPerDeath = -4.0; // A death makes you lose 4 power
	
	public static double powerPerHourOnline = 10.0;
	public static double powerPerHourOffline = 0.0;
	
	// players will no longer lose power from being offline once their power drops to this amount or less
	public static double powerLimitGainOnline = 0.0;
	public static double powerLimitGainOffline = 0.0;
	public static double powerLimitLossOnline = 0.0;
	public static double powerLimitLossOffline = 0.0;
	
	public static boolean scaleNegativePower = false; // Power regeneration rate increase as power decreases
	public static double scaleNegativeDivisor = 40.0; // Divisor for inverse power regeneration curve
	
	public static double powerFactionMax = 0.0;  // if greater than 0, the cap on how much power a faction can have (additional power from players beyond that will act as a "buffer" of sorts)
	
	
	
	public static String prefixLeader = "**";
	public static String prefixOfficer = "*";
	public static String prefixMember = "+";
	public static String prefixRecruit = "-";
	
	public static int factionTagLengthMin = 3;
	public static int factionTagLengthMax = 10;
	public static boolean factionTagForceUpperCase = false;
	
	public static boolean newFactionsDefaultOpen = false;

	// when faction membership hits this limit, players will no longer be able to join using /f join; default is 0, no limit
	public static int factionMemberLimit = 0;

	// what faction ID to start new players in when they first join the server; default is 0, "no faction"
	public static String newPlayerStartingFactionID = Const.FACTIONID_NONE;

	//public static boolean showMapFactionKey = true;
	//public static boolean showNeutralFactionsOnMap = true;
	//public static boolean showEnemyFactionsOnMap = true;
	
	// Disallow joining/leaving/kicking while power is negative
	public static boolean canLeaveWithNegativePower = true;
	
	// Configuration on the Faction tag in chat messages.
	public static boolean chatSetFormat = false;
	public static String chatSetFormatTo = "<{faction_relcolor}§l{faction_roleprefix}§r{faction_relcolor}{faction_tag_pr}"+ChatColor.WHITE.toString()+"%s> %s";
	public static boolean chatParseTags = true;
	public static boolean chatParseTagsColored = false;
	public static Map<String, String> chatSingleFormats = new HashMap<String, String>();
	public static String chatTagFormat = "%s"+ChatColor.WHITE; // This one is almost deprecated now right? or is it?
	
	// Herochat
	public static String herochatFactionName = "Faction";
	public static String herochatFactionNick = "F";
	public static String herochatFactionFormat = "{color}[&l{nick}&r{color} &l{faction_roleprefix}&r{color}{faction_title_pr}{sender}{color}] &f{msg}";
	public static ChatColor herochatFactionColor = ChatColor.GREEN;
	public static int herochatFactionDistance = 0;
	public static boolean herochatFactionIsShortcutAllowed = false;
	public static boolean herochatFactionCrossWorld = true;
	public static boolean herochatFactionMuted = false;
	public static Set<String> herochatFactionWorlds = new HashSet<String>();
	
	public static String herochatAlliesName = "Allies";
	public static String herochatAlliesNick = "A";
	public static String herochatAlliesFormat = "{color}[&l{nick}&r&f {faction_relcolor}&l{faction_roleprefix}&r{faction_relcolor}{faction_tag_pr}{sender}{color}] &f{msg}";
	public static ChatColor herochatAlliesColor = ChatColor.DARK_PURPLE;
	public static int herochatAlliesDistance = 0;
	public static boolean herochatAlliesIsShortcutAllowed = false;
	public static boolean herochatAlliesCrossWorld = true;
	public static boolean herochatAlliesMuted = false;
	public static Set<String> herochatAlliesWorlds = new HashSet<String>();
	
	public static String herochatAllyName = "Allies";

	// TODO: Does anyone toggle this feature on I wonder?
	// It could work for small servers but never for big ones.
	// Why not conform to big server setups at once?
	// POSSIBLY: Remove this option
	public static boolean broadcastDescriptionChanges = false;

	public static double autoLeaveAfterDaysOfInactivity = 10.0;
	public static double autoLeaveRoutineRunsEveryXMinutes = 5.0;
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
	public static boolean logPlayerCommands = true;

	// prevent some potential exploits
	public static boolean handleExploitObsidianGenerators = true;
	public static boolean handleExploitEnderPearlClipping = true;
	public static boolean handleExploitInteractionSpam = true;
	public static boolean handleExploitTNTWaterlog = false;

	public static boolean homesEnabled = true;
	public static boolean homesMustBeInClaimedTerritory = true;
	public static boolean homesTeleportCommandEnabled = true;
	public static boolean homesTeleportAllowedFromEnemyTerritory = true;
	public static boolean homesTeleportAllowedFromDifferentWorld = true;
	public static double homesTeleportAllowedEnemyDistance = 32.0;
	public static boolean homesTeleportIgnoreEnemiesIfInOwnTerritory = true;
	
	// TODO: This one should not be in the config should it? It should be built into the enum?
	public static Rel friendlyFireFromRel = Rel.TRUCE;
	
	public static boolean disablePVPForFactionlessPlayers = false;
	public static boolean enablePVPAgainstFactionlessInAttackersLand = false;

	//public static boolean peacefulMembersDisablePowerLoss = true;
	
	public static boolean permanentFactionsDisableLeaderPromotion = false;
	
	public static boolean claimsMustBeConnected = false;
	public static boolean claimingFromOthersAllowed = true;
	public static boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = true;
	public static int claimsRequireMinFactionMembers = 1;
	public static int claimedLandsMax = 0;

	// if someone is doing a radius claim and the process fails to claim land this many times in a row, it will exit
	public static int radiusClaimFailureLimit = 9;

	//public static double considerFactionsReallyOfflineAfterXMinutes = 0.0;
	
	public static int actionDeniedPainAmount = 2;

	// commands which will be prevented if the player is a member of a permanent faction
	public static Set<String> permanentFactionMemberDenyCommands = new LinkedHashSet<String>();

	// commands which will be prevented when in claimed territory of another faction
	public static Set<String> territoryNeutralDenyCommands = new LinkedHashSet<String>();
	public static Set<String> territoryEnemyDenyCommands = MUtil.set("home", "sethome", "spawn", "tpahere", "tpaccept", "tpa", "warp");
	
	public static double territoryShieldFactor = 0.3;

	// for claimed areas where further faction-member ownership can be defined

	public static boolean pistonProtectionThroughDenyBuild = true;	

	// Spout features
	public static boolean spoutFactionTagsOverNames = true;  // show faction tags over names over player heads
	public static boolean spoutFactionTitlesOverNames = true;  // whether to include player's title in that
	public static boolean spoutHealthBarUnderNames = true;  // Show healthbar under player names.
	public static String spoutHealthBarLeft = "{c}[";
	public static String spoutHealthBarSolid = "|";
	public static String spoutHealthBarBetween = "&8";
	public static String spoutHealthBarEmpty = "|";
	public static String spoutHealthBarRight = "{c}]";
	public static double spoutHealthBarSolidsPerEmpty = 1d;
	public static String spoutHealthBarColorTag = "{c}";
	public static int spoutHealthBarWidth = 30;
	
	public static Map<Double, String> spoutHealthBarColorUnderQuota = MUtil.map(
		1.0d, "&2",
		0.8d, "&a",
		0.5d, "&e",
		0.4d, "&6",
		0.3d, "&c",
		0.2d, "&4"
	);
	
	public static boolean spoutCapes = true;  // Show faction capes
	public static int spoutTerritoryDisplayPosition = 1;  // permanent territory display, instead of by chat; 0 = disabled, 1 = top left, 2 = top center, 3+ = top right
	public static float spoutTerritoryDisplaySize = 1.0f;  // text scale (size) for territory display
	public static boolean spoutTerritoryDisplayShowDescription = true;  // whether to show the faction description, not just the faction tag
	public static boolean spoutTerritoryAccessShow = true;  // show occasional territory access info as well ("access granted" or "access restricted" if relevant)
	public static boolean spoutTerritoryNoticeShow = true;  // show additional brief territory notice near center of screen, to be sure player notices transition
	public static int spoutTerritoryNoticeTop = 40;  // how far down the screen to place the additional notice
	public static boolean spoutTerritoryNoticeShowDescription = false;  // whether to show the faction description in the notice, not just the faction tag
	public static float spoutTerritoryNoticeSize = 1.5f;  // text scale (size) for notice
	public static float spoutTerritoryNoticeLeaveAfterSeconds = 2.00f;  // how many seconds before the notice goes away
	
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

	public static int    econLandRewardTaskRunsEveryXMinutes = 20;
	public static double econLandReward = 0.00;
	
	//Faction banks, to pay for land claiming and other costs instead of individuals paying for them
	public static boolean bankEnabled = true;
	//public static boolean bankMembersCanWithdraw = false; //Have to be at least moderator to withdraw or pay money to another faction
	public static boolean bankFactionPaysCosts = true; //The faction pays for faction command costs, such as sethome
	public static boolean bankFactionPaysLandCosts = true; //The faction pays for land claiming costs.

	// mainly for other plugins/mods that use a fake player to take actions, which shouldn't be subject to our protections
	public static Set<String> playersWhoBypassAllProtection = new LinkedHashSet<String>();

	public static Set<String> worldsNoClaiming = new LinkedHashSet<String>();
	
	// TODO: Should worldsNoPowerLoss rather be a bukkit permission node?
	public static Set<String> worldsNoPowerLoss = new LinkedHashSet<String>();
	public static Set<String> worldsIgnorePvP = new LinkedHashSet<String>();
	// TODO: A better solution Would be to have One wilderness faction per world.
	//public static Set<String> worldsNoWildernessProtection = new LinkedHashSet<String>();
	
	static
	{
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
		
		chatSingleFormats.put("pl", " %s");
		chatSingleFormats.put("pr", "%s ");
		chatSingleFormats.put("pb", " %s ");
	}
}

