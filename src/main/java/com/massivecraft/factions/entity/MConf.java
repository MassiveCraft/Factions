package com.massivecraft.factions.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;

import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.factions.integration.dynmap.DynmapStyle;
import com.massivecraft.factions.listeners.FactionsListenerChat;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.MUtil;

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
		
		FactionsListenerChat.get().setup();
		
		return this;
	}
	
	// -------------------------------------------- //
	// COMMAND ALIASES
	// -------------------------------------------- //
	
	public List<String> aliasesF = MUtil.list("f");
	
	// -------------------------------------------- //
	// TASKS
	// -------------------------------------------- //
	
	public double taskPlayerPowerUpdateMinutes = 1;
	public double taskPlayerDataRemoveMinutes = 5;
	public double taskEconLandRewardMinutes = 20;
	
	// -------------------------------------------- //
	// REMOVE DATA
	// -------------------------------------------- //
	
	public boolean removePlayerDataWhenBanned = true;
	public double removePlayerDataAfterInactiveDays = 20.0;
	
	// -------------------------------------------- //
	// SPECIAL FACTION IDS
	// -------------------------------------------- //
	
	public String factionIdNone = UUID.randomUUID().toString();
	public String factionIdSafezone = UUID.randomUUID().toString();
	public String factionIdWarzone = UUID.randomUUID().toString();
	
	// -------------------------------------------- //
	// DEFAULTS
	// -------------------------------------------- //
	
	public String defaultPlayerFactionId = this.factionIdNone;
	public Rel defaultPlayerRole = Rel.RECRUIT;
	public double defaultPlayerPower = 0.0;
	
	public boolean defaultFactionOpen = false;
	public Map<FFlag, Boolean> defaultFactionFlags = FFlag.getDefaultDefaults();
	public Map<FPerm, Set<Rel>> defaultFactionPerms = FPerm.getDefaultDefaults();

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
	// MESSAGES
	// -------------------------------------------- //
	
	public boolean broadcastNameChange = false;
	
	// -------------------------------------------- //
	// CLAIM LIMITS
	// -------------------------------------------- //
	
	// if someone is doing a radius claim and the process fails to claim land this many times in a row, it will exit
	public int radiusClaimFailureLimit = 9;
	
	// the maximum radius allowed when using the claim command.
	public int radiusClaimRadiusLimit = 5;
	
	// -------------------------------------------- //
	// CLAIMS
	// -------------------------------------------- //
	
	public boolean claimsMustBeConnected = true;
	public boolean claimingFromOthersAllowed = true;
	public boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = false;
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
	public double territoryShieldFactor = 0.3D;
	
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
	// DERPY OVERRIDES
	// -------------------------------------------- //
	// TODO: Should worldsNoPowerLoss rather be a bukkit permission node?
	// TODO: These are derpy because they possibly use an invalid design approach.
	// After universe support is added. Would some of these be removed?
	// Could it also be more customizeable using some sort of permission lookup map?
	
	// mainly for other plugins/mods that use a fake player to take actions, which shouldn't be subject to our protections
	public Set<String> playersWhoBypassAllProtection = new LinkedHashSet<String>();

	public Set<String> worldsNoClaiming = new LinkedHashSet<String>();
	public Set<String> getWorldsNoClaiming()
	{
		Set<String> ret = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		ret.addAll(this.worldsNoClaiming);
		return ret;
	}
	
	public Set<String> worldsNoPowerLoss = new LinkedHashSet<String>();
	public Set<String> getWorldsNoPowerLoss()
	{
		Set<String> ret = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		ret.addAll(this.worldsNoPowerLoss);
		return ret;
	}
	
	public Set<String> worldsIgnorePvP = new LinkedHashSet<String>();
	public Set<String> getWorldsIgnlorePvP()
	{
		Set<String> ret = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		ret.addAll(this.worldsIgnorePvP);
		return ret;
	}
	
	// -------------------------------------------- //
	// EXPLOITS
	// -------------------------------------------- //
	
	public boolean handleExploitObsidianGenerators = true;
	public boolean handleExploitEnderPearlClipping = true;
	public boolean handleExploitTNTWaterlog = false;
	
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
	
	public Map<EventFactionsChunkChangeType, Boolean> lwcRemoveOnChange = MUtil.map(
		EventFactionsChunkChangeType.BUY, false,
		EventFactionsChunkChangeType.SELL, false,
		EventFactionsChunkChangeType.CONQUER, false,
		EventFactionsChunkChangeType.PILLAGE, false
	);
	
	public Map<Rel, Boolean> lwcAllowIn = MUtil.map(
		Rel.ALLY, false,
		Rel.ENEMY, false,
		Rel.NEUTRAL, false,
		Rel.TRUCE, false
	);
	
	public Set<String> lwcDisallowIn = MUtil.set(
		MConf.get().factionIdWarzone,
		MConf.get().factionIdSafezone,
		MConf.get().factionIdNone
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
	public double econCostOpen = 0.0;
	
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
	
	// -------------------------------------------- //
	// INTEGRATION: DYNMAP
	// -------------------------------------------- //
	
	// Should the dynmap intagration be used?
	public boolean dynmapUse = true;
	
	// Should the dynmap updates be logged to console output?
	public boolean dynmapUpdateLog = false;
	
	// Name of the Factions layer
	public String dynmapLayerName = "Factions";
	
	// Should the layer be visible per default
	public boolean dynmapLayerVisible = true;
	
	// Ordering priority in layer menu (low goes before high - default is 0)
	public int dynmapLayerPriority = 2;
	
	// (optional) set minimum zoom level before layer is visible (0 = defalt, always visible)
	public int dynmapLayerMinimumZoom = 0;
	
	// Format for popup - substitute values for macros
	//public String dynmapInfowindowFormat = "<div class=\"infowindow\"><span style=\"font-size:120%;\">%regionname%</span><br />Flags<br /><span style=\"font-weight:bold;\">%flags%</span></div>";
	public String dynmapDescription = 
		"<div class=\"infowindow\">\n" +
		"<span style=\"font-weight: bold; font-size: 150%;\">%name%</span></br>\n" +
		"<span style=\"font-style: italic; font-size: 110%;\">%description%</span></br>\n" +
		"</br>\n" +
		"<span style=\"font-weight: bold;\">Leader:</span> %players.leader%</br>\n" +
		"<span style=\"font-weight: bold;\">Officers:</span> %players.officers.count%</br>\n" +
		"<span style=\"font-weight: bold;\">Members:</span> %players.members.count%</br>\n" +
		"<span style=\"font-weight: bold;\">Recruits:</span> %players.recruits.count%</br>\n" +
		"<span style=\"font-weight: bold;\">TOTAL:</span> %players.count%</br>\n" +
		"</br>\n" +
		"<span style=\"font-weight: bold;\">Age:</span> %age%</br>\n" +
		"<span style=\"font-weight: bold;\">Bank:</span> %money%</br>\n" +
		"</br>\n" +
		"<span style=\"font-weight: bold;\">Flags:</span></br>\n" +
		"%open.color% | %permanent.color% | %peaceful.color% | %infpower.color% | %powerloss.color%</br>\n" +
		"%pvp.color% | %friendlyfire.color% | %monsters.color% | %explosions.color%</br>\n" +
		"%offlineexplosions.color% | %firespread.color% | %endergrief.color%\n" +
		"</div>";
	
	// Enable the %money% macro. Only do this if you know your economy manager is thread safe.
	public boolean dynmapDescriptionMoney = false;
	
	// Allow players in faction to see one another on Dynmap (only relevant if Dynmap has 'player-info-protected' enabled)
	public boolean dynmapVisibilityByFaction = true;
	
	// Optional setting to limit which regions to show.
	// If empty all regions are shown.
	// Specify Faction either by name or UUID.
	// To show all regions on a given world, add 'world:<worldname>' to the list.
	public Set<String> dynmapVisibleFactions = new LinkedHashSet<String>();
	  
	// Optional setting to hide specific Factions.
	// Specify Faction either by name or UUID.
	// To hide all regions on a given world, add 'world:<worldname>' to the list.
	public Set<String> dynmapHiddenFactions = new LinkedHashSet<String>();
	
	// Region Style
	public final static transient String DYNMAP_STYLE_LINE_COLOR = "#00FF00";
	public final static transient double DYNMAP_STYLE_LINE_OPACITY = 0.8D;
	public final static transient int DYNMAP_STYLE_LINE_WEIGHT = 3;
	public final static transient String DYNMAP_STYLE_FILL_COLOR = "#00FF00";
	public final static transient double DYNMAP_STYLE_FILL_OPACITY = 0.35D;
	public final static transient String DYNMAP_STYLE_HOME_MARKER = "greenflag";
	public final static transient boolean DYNMAP_STYLE_BOOST = false;
	
	public DynmapStyle dynmapDefaultStyle = new DynmapStyle()
	.setStrokeColor(DYNMAP_STYLE_LINE_COLOR)
	.setLineOpacity(DYNMAP_STYLE_LINE_OPACITY)
	.setLineWeight(DYNMAP_STYLE_LINE_WEIGHT)
	.setFillColor(DYNMAP_STYLE_FILL_COLOR)
	.setFillOpacity(DYNMAP_STYLE_FILL_OPACITY)
	.setHomeMarker(DYNMAP_STYLE_HOME_MARKER)
	.setBoost(DYNMAP_STYLE_BOOST);
	
	// Optional per Faction style overrides. Any defined replace those in dynmapDefaultStyle.
	// Specify Faction either by name or UUID.
	public Map<String, DynmapStyle> dynmapFactionStyles = MUtil.map(
		"SafeZone", new DynmapStyle().setStrokeColor("#FF00FF").setFillColor("#FF00FF").setBoost(false),
		"WarZone", new DynmapStyle().setStrokeColor("#FF0000").setFillColor("#FF0000").setBoost(false)
	);

}