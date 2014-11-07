package com.massivecraft.factions.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.WorldExceptionSet;
import com.massivecraft.factions.engine.EngineChat;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.TimeUnit;

public class MConf extends Entity<MConf>
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	protected static transient MConf i;
	public static MConf get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: ENTITY
	// -------------------------------------------- //
	
	@Override
	public MConf load(MConf that)
	{
		super.load(that);
		
		if (!Factions.get().isDatabaseInitialized()) return this;
		
		EngineChat.get().deactivate();
		EngineChat.get().activate();
		
		return this;
	}
	
	// -------------------------------------------- //
	// COMMAND ALIASES
	// -------------------------------------------- //
	
	public List<String> aliasesF = MUtil.list("f");
	
	// -------------------------------------------- //
	// WORLDS FEATURE ENABLED
	// -------------------------------------------- //
	
	public WorldExceptionSet worldsClaimingEnabled = new WorldExceptionSet();
	public WorldExceptionSet worldsPowerLossEnabled = new WorldExceptionSet();
	public WorldExceptionSet worldsPvpRulesEnabled = new WorldExceptionSet();
	
	// -------------------------------------------- //
	// DERPY OVERRIDES
	// -------------------------------------------- //
	
	// mainly for other plugins/mods that use a fake player to take actions, which shouldn't be subject to our protections
	public Set<String> playersWhoBypassAllProtection = new LinkedHashSet<String>();
	
	// -------------------------------------------- //
	// TASKS
	// -------------------------------------------- //
	
	public double taskPlayerPowerUpdateMinutes = 1;
	public double taskPlayerDataRemoveMinutes = 5;
	public double taskEconLandRewardMinutes = 20;
	
	// -------------------------------------------- //
	// REMOVE DATA
	// -------------------------------------------- //
	
	public boolean removePlayerWhenBanned = true;
	
	// The Default
	public long removePlayerMillisDefault = 10 * TimeUnit.MILLIS_PER_DAY;
	
	// Player Age Bonus
	public Map<Long, Long> removePlayerMillisPlayerAgeToBonus = MUtil.map(
		2 * TimeUnit.MILLIS_PER_WEEK, 10 * TimeUnit.MILLIS_PER_DAY  // +10 after 2 weeks
	);
	
	// Faction Age Bonus
	public Map<Long, Long> removePlayerMillisFactionAgeToBonus = MUtil.map(
		4 * TimeUnit.MILLIS_PER_WEEK, 10 * TimeUnit.MILLIS_PER_DAY, // +10 after 4 weeks
		2 * TimeUnit.MILLIS_PER_WEEK,  5 * TimeUnit.MILLIS_PER_DAY  // +5 after 2 weeks
	);
	
	// -------------------------------------------- //
	// SPECIAL FACTION IDS
	// -------------------------------------------- //
	// These are a deprecated remnant from the universe system.
	// We needed these to understand the difference between wilderness in different universes.
	// Now that we are back to one universe only, we can have static names like simply "none", "safezone" and "warzone".
	// Previously we set them to UUID.randomUUID().toString() but now we set them to null.
	// If the value is set we use it to update map entries and then set it to null really quick.
	
	public String factionIdNone = null;
	public String factionIdSafezone = null;
	public String factionIdWarzone = null;
	
	// -------------------------------------------- //
	// DEFAULTS
	// -------------------------------------------- //
	
	public String defaultPlayerFactionId = Factions.ID_NONE;
	public Rel defaultPlayerRole = Rel.RECRUIT;
	public double defaultPlayerPower = 0.0;
	
	// -------------------------------------------- //
	// MOTD
	// -------------------------------------------- //
	
	public EventPriority motdPriority = EventPriority.NORMAL;
	public int motdDelayTicks = -1;

	// -------------------------------------------- //
	// POWER
	// -------------------------------------------- //
	
	public double powerMax = 10.0;
	public double powerMin = 0.0;
	public double powerPerHour = 2.0;
	public double powerPerDeath = -2.0;
	
	public boolean canLeaveWithNegativePower = true;
	
	// -------------------------------------------- //
	// CORE
	// -------------------------------------------- //

	public int factionMemberLimit = 0;
	public double factionPowerMax = 0.0;
	
	public int factionNameLengthMin = 3;
	public int factionNameLengthMax = 16;
	public boolean factionNameForceUpperCase = false;
	
	// -------------------------------------------- //
	// SET LIMITS
	// -------------------------------------------- //
	
	public int setRadiusMax = 30;
	public int setFillMax = 1000;
	
	// -------------------------------------------- //
	// CLAIMS
	// -------------------------------------------- //
	
	public boolean claimsMustBeConnected = true;
	public boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = false;
	
	public boolean claimingFromOthersAllowed = true;
	
	// 0 means you can claim just next to others
	// 1 means you must have a single chunk of padding in between.
	public int claimMinimumChunksDistanceToOthers = 0; 
	
	public int claimsRequireMinFactionMembers = 1;
	public int claimedLandsMax = 0;
	
	// -------------------------------------------- //
	// HOMES
	// -------------------------------------------- //
	
	public boolean homesEnabled = true;
	public boolean homesMustBeInClaimedTerritory = true;
	public boolean homesTeleportCommandEnabled = true;
	public boolean homesTeleportAllowedFromEnemyTerritory = true;
	public boolean homesTeleportAllowedFromDifferentWorld = true;
	public double homesTeleportAllowedEnemyDistance = 32.0;
	public boolean homesTeleportIgnoreEnemiesIfInOwnTerritory = true;
	
	public boolean homesTeleportToOnDeathActive = false;
	public EventPriority homesTeleportToOnDeathPriority = EventPriority.NORMAL;
	
	// -------------------------------------------- //
	// ASSORTED
	// -------------------------------------------- //
	
	public boolean permanentFactionsDisableLeaderPromotion = false;
	public double actionDeniedPainAmount = 2.0D;
	public boolean disablePVPForFactionlessPlayers = false;
	public boolean enablePVPAgainstFactionlessInAttackersLand = false;
	public double territoryShieldFactor = 0.1D;
	
	// -------------------------------------------- //
	// DENY COMMANDS
	// -------------------------------------------- //
	
	// commands which will be prevented if the player is a member of a permanent faction
	public List<String> denyCommandsPermanentFactionMember = new ArrayList<String>();

	// commands which will be prevented when in claimed territory of another faction
	public Map<Rel, List<String>> denyCommandsTerritoryRelation = MUtil.map(
		Rel.ENEMY, MUtil.list(
			// Essentials commands
			"home",
			"homes",
			"sethome",
			"createhome",
			"tpahere",
			"tpaccept",
			"tpyes",
			"tpa",
			"call",
			"tpask",
			"warp",
			"warps",
			"spawn",
			// Essentials e-alliases
			"ehome",
			"ehomes",
			"esethome",
			"ecreatehome",
			"etpahere",
			"etpaccept",
			"etpyes",
			"etpa",
			"ecall",
			"etpask",
			"ewarp",
			"ewarps",
			"espawn",
			// Essentials fallback alliases
			"essentials:home",
			"essentials:homes",
			"essentials:sethome",
			"essentials:createhome",
			"essentials:tpahere",
			"essentials:tpaccept",
			"essentials:tpyes",
			"essentials:tpa",
			"essentials:call",
			"essentials:tpask",
			"essentials:warp",
			"essentials:warps",
			"essentials:spawn",
			// Other plugins
			"wtp",
			"uspawn",
			"utp",
			"mspawn",
			"mtp",
			"fspawn",
			"ftp",
			"jspawn",
			"jtp"
		),
		Rel.NEUTRAL, new ArrayList<String>(),
		Rel.TRUCE, new ArrayList<String>(),
		Rel.ALLY, new ArrayList<String>(),
		Rel.MEMBER, new ArrayList<String>()
	);
	
	// -------------------------------------------- //
	// CHAT
	// -------------------------------------------- //
	
	// We offer a simple standard way to set the format
	public boolean chatSetFormat = false;
	public EventPriority chatSetFormatAt = EventPriority.LOWEST;
	public String chatSetFormatTo = "<{factions_relcolor}§l{factions_roleprefix}§r{factions_relcolor}{factions_name|rp}§f%1$s> %2$s";
	
	// We offer a simple standard way to parse the chat tags
	public boolean chatParseTags = true;
	public EventPriority chatParseTagsAt = EventPriority.LOW;
	
	// -------------------------------------------- //
	// COLORS
	// -------------------------------------------- //
	
	public ChatColor colorMember = ChatColor.GREEN;
	public ChatColor colorAlly = ChatColor.DARK_PURPLE;
	public ChatColor colorTruce = ChatColor.LIGHT_PURPLE;
	public ChatColor colorNeutral = ChatColor.WHITE;
	public ChatColor colorEnemy = ChatColor.RED;
	
	public ChatColor colorNoPVP = ChatColor.GOLD;
	public ChatColor colorFriendlyFire = ChatColor.DARK_RED;
	//public ChatColor colorWilderness = ChatColor.DARK_GREEN;
	
	// -------------------------------------------- //
	// PREFIXES
	// -------------------------------------------- //
	
	public String prefixLeader = "**";
	public String prefixOfficer = "*";
	public String prefixMember = "+";
	public String prefixRecruit = "-";
	
	// -------------------------------------------- //
	// EXPLOITS
	// -------------------------------------------- //
	
	public boolean handleExploitObsidianGenerators = true;
	public boolean handleExploitEnderPearlClipping = true;
	public boolean handleExploitTNTWaterlog = false;
	
	// -------------------------------------------- //
	// SEE CHUNK
	// -------------------------------------------- //
	
	// Use 1 or multiple of 3, 4 or 5.
	public int seeChunkSteps = 1;
	
	// White/Black List for creating sparse patterns.
	public int seeChunkKeepEvery = 5;
	public int seeChunkSkipEvery = 0;
	
	public long seeChunkPeriodMillis = 500;
	public int seeChunkParticleAmount = 30;
	public float seeChunkParticleOffsetY = 2;
	public float seeChunkParticleDeltaY = 2;
	
	// -------------------------------------------- //
	// LOGGING
	// -------------------------------------------- //
	
	public boolean logFactionCreate = true;
	public boolean logFactionDisband = true;
	public boolean logFactionJoin = true;
	public boolean logFactionKick = true;
	public boolean logFactionLeave = true;
	public boolean logLandClaims = true;
	public boolean logLandUnclaims = true;
	public boolean logMoneyTransactions = true;
	public boolean logPlayerCommands = true;
	
	// -------------------------------------------- //
	// ENUMERATIONS
	// -------------------------------------------- //
	
	public Set<Material> materialsEditOnInteract = MUtil.set(
		Material.DIODE_BLOCK_OFF,
		Material.DIODE_BLOCK_ON,
		Material.NOTE_BLOCK,
		Material.CAULDRON,
		Material.SOIL
	);
	
	public Set<Material> materialsEditTools = MUtil.set(
		Material.FIREBALL,
		Material.FLINT_AND_STEEL,
		Material.BUCKET,
		Material.WATER_BUCKET,
		Material.LAVA_BUCKET
	);
	
	// The duplication bug found in Spigot 1.8 protocol patch
	// https://github.com/MassiveCraft/Factions/issues/693
	public Set<Material> materialsEditToolsDupeBug = MUtil.set(
		Material.CHEST,
		Material.SIGN_POST,
		Material.TRAPPED_CHEST,
		Material.SIGN,
		Material.WOOD_DOOR,
		Material.IRON_DOOR
	);
	
	public Set<Material> materialsDoor = MUtil.set(
		Material.WOODEN_DOOR,
		Material.TRAP_DOOR,
		Material.FENCE_GATE
	);
	
	public Set<Material> materialsContainer = MUtil.set(
		Material.DISPENSER,
		Material.CHEST,
		Material.FURNACE,
		Material.BURNING_FURNACE,
		Material.JUKEBOX,
		Material.BREWING_STAND,
		Material.ENCHANTMENT_TABLE,
		Material.ANVIL,
		Material.BEACON,
		Material.TRAPPED_CHEST,
		Material.HOPPER,
		Material.DROPPER
	);
	
	public Set<EntityType> entityTypesContainer = MUtil.set(
		EntityType.MINECART_CHEST,
		EntityType.MINECART_HOPPER
	);
	
	public Set<EntityType> entityTypesMonsters = MUtil.set(
		EntityType.BLAZE,
		EntityType.CAVE_SPIDER,
		EntityType.CREEPER,
		EntityType.ENDERMAN,
		EntityType.ENDER_DRAGON,
		EntityType.GHAST,
		EntityType.GIANT,
		EntityType.MAGMA_CUBE,
		EntityType.PIG_ZOMBIE,
		EntityType.SILVERFISH,
		EntityType.SKELETON,
		EntityType.SLIME,
		EntityType.SPIDER,
		EntityType.WITCH,
		EntityType.WITHER,
		EntityType.ZOMBIE
	);
	
	// -------------------------------------------- //
	// INTEGRATION: HeroChat
	// -------------------------------------------- //
	
	// HeroChat: The Faction Channel
	public String herochatFactionName = "Faction";
	public String herochatFactionNick = "F";
	public String herochatFactionFormat = "{color}[&l{nick}&r{color} &l{factions_roleprefix}&r{color}{factions_title|rp}{sender}{color}] &f{msg}";
	public ChatColor herochatFactionColor = ChatColor.GREEN;
	public int herochatFactionDistance = 0;
	public boolean herochatFactionIsShortcutAllowed = false;
	public boolean herochatFactionCrossWorld = true;
	public boolean herochatFactionMuted = false;
	public Set<String> herochatFactionWorlds = new HashSet<String>();
	
	// HeroChat: The Allies Channel
	public String herochatAlliesName = "Allies";
	public String herochatAlliesNick = "A";
	public String herochatAlliesFormat = "{color}[&l{nick}&r&f {factions_relcolor}&l{factions_roleprefix}&r{factions_relcolor}{factions_name|rp}{sender}{color}] &f{msg}";
	public ChatColor herochatAlliesColor = ChatColor.DARK_PURPLE;
	public int herochatAlliesDistance = 0;
	public boolean herochatAlliesIsShortcutAllowed = false;
	public boolean herochatAlliesCrossWorld = true;
	public boolean herochatAlliesMuted = false;
	public Set<String> herochatAlliesWorlds = new HashSet<String>();
	
	// -------------------------------------------- //
	// INTEGRATION: LWC
	// -------------------------------------------- //
	
	public boolean lwcMustHaveBuildRightsToCreate = true;
	public boolean lwcRemoveIfNoBuildRights = false;
	
	public Map<EventFactionsChunkChangeType, Boolean> lwcRemoveOnChange = MUtil.map(
		EventFactionsChunkChangeType.BUY, false,
		EventFactionsChunkChangeType.SELL, false,
		EventFactionsChunkChangeType.CONQUER, false,
		EventFactionsChunkChangeType.PILLAGE, false
	);
	
	// -------------------------------------------- //
	// INTEGRATION: ECONOMY
	// -------------------------------------------- //
	
	public boolean econEnabled = false;
	
	// TODO: Rename to include unit.
	public double econLandReward = 0.00;
	
	public String econUniverseAccount = "";
	
	public Map<EventFactionsChunkChangeType, Double> econChunkCost = MUtil.map(
		EventFactionsChunkChangeType.BUY, 30.0,
		EventFactionsChunkChangeType.SELL, -20.0,
		EventFactionsChunkChangeType.CONQUER, -10.0,
		EventFactionsChunkChangeType.PILLAGE, -10.0
	);
	
	public double econCostCreate = 200.0;
	public double econCostSethome = 0.0;
	public double econCostJoin = 0.0;
	public double econCostLeave = 0.0;
	public double econCostKick = 0.0;
	public double econCostInvite = 0.0;
	public double econCostDeinvite = 0.0;
	public double econCostHome = 0.0;
	public double econCostName = 0.0;
	public double econCostDescription = 0.0;
	public double econCostTitle = 0.0;
	public double econCostFlag = 0.0;
	
	public Map<Rel, Double> econRelCost = MUtil.map(
		Rel.ENEMY, 0.0,
		Rel.ALLY, 0.0,
		Rel.TRUCE, 0.0,
		Rel.NEUTRAL, 0.0
	);
	
	//Faction banks, to pay for land claiming and other costs instead of individuals paying for them
	public boolean bankEnabled = true;
	//public static boolean bankMembersCanWithdraw = false; //Have to be at least moderator to withdraw or pay money to another faction
	public boolean bankFactionPaysCosts = true; //The faction pays for faction command costs, such as sethome
	public boolean bankFactionPaysLandCosts = true; //The faction pays for land claiming costs.

}