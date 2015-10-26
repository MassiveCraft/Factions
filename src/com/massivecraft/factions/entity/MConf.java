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
import com.massivecraft.factions.engine.EngineChat;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.massivecore.collections.BackstringEnumSet;
import com.massivecraft.massivecore.collections.WorldExceptionSet;
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
	
	// Don't you want "f" as the base command alias? Simply change it here.
	public List<String> aliasesF = MUtil.list("f");
	
	// -------------------------------------------- //
	// WORLDS FEATURE ENABLED
	// -------------------------------------------- //
	
	// Use this blacklist/whitelist system to toggle features on a per world basis.
	// Do you only want claiming enabled on the one map called "Hurr"?
	// In such case set standard to false and add "Hurr" as an exeption to worldsClaimingEnabled.
	public WorldExceptionSet worldsClaimingEnabled = new WorldExceptionSet();
	public WorldExceptionSet worldsPowerLossEnabled = new WorldExceptionSet();
	public WorldExceptionSet worldsPvpRulesEnabled = new WorldExceptionSet();
	
	// -------------------------------------------- //
	// DERPY OVERRIDES
	// -------------------------------------------- //
	
	// Add player names here who should bypass all protections.
	// Should /not/ be used for admins. There is "/f adminmode" for that.
	// This is for other plugins/mods that use a fake player to take actions, which shouldn't be subject to our protections.
	public Set<String> playersWhoBypassAllProtection = new LinkedHashSet<String>();
	
	// -------------------------------------------- //
	// TASKS
	// -------------------------------------------- //
	
	// Define the time in minutes between certain Factions system tasks is ran.
	public double taskPlayerPowerUpdateMinutes = 1;
	public double taskPlayerDataRemoveMinutes = 5;
	public double taskEconLandRewardMinutes = 20;
	
	// -------------------------------------------- //
	// REMOVE DATA
	// -------------------------------------------- //
	
	// Should players be kicked from their faction and their data erased when banned?
	public boolean removePlayerWhenBanned = true;
	
	// After how many milliseconds should players be automatically kicked from their faction?
	
	// The Default
	public long removePlayerMillisDefault = 10 * TimeUnit.MILLIS_PER_DAY; // 10 days
	
	// Player Age Bonus
	public Map<Long, Long> removePlayerMillisPlayerAgeToBonus = MUtil.map(
		2 * TimeUnit.MILLIS_PER_WEEK, 10 * TimeUnit.MILLIS_PER_DAY  // +10 days after 2 weeks
	);
	
	// Faction Age Bonus
	public Map<Long, Long> removePlayerMillisFactionAgeToBonus = MUtil.map(
		4 * TimeUnit.MILLIS_PER_WEEK, 10 * TimeUnit.MILLIS_PER_DAY, // +10 days after 4 weeks
		2 * TimeUnit.MILLIS_PER_WEEK,  5 * TimeUnit.MILLIS_PER_DAY  // +5 days after 2 weeks
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
	
	// Which faction should new players be followers of?
	// "none" means Wilderness. Remember to specify the id, like "3defeec7-b3b1-48d9-82bb-2a8903df24e3" and not the name.
	public String defaultPlayerFactionId = Factions.ID_NONE;
	
	// What rank should new players joining a faction get?
	// If not RECRUIT then MEMBER might make sense.
	public Rel defaultPlayerRole = Rel.RECRUIT;
	
	// What power should the player start with?
	public double defaultPlayerPower = 0.0;
	
	// -------------------------------------------- //
	// MOTD
	// -------------------------------------------- //
	
	// During which event priority should the faction message of the day be displayed?
	// Choose between: LOWEST, LOW, NORMAL, HIGH, HIGHEST and MONITOR.
	// This setting only matters if "motdDelayTicks" is set to -1
	public EventPriority motdPriority = EventPriority.NORMAL;
	
	// How many ticks should we delay the faction message of the day with?
	// -1 means we don't delay at all. We display it at once.
	// 0 means it's deferred to the upcomming server tick.
	// 5 means we delay it yet another 5 ticks.
	public int motdDelayTicks = -1;

	// -------------------------------------------- //
	// POWER
	// -------------------------------------------- //
	
	// What is the maximum player power?
	public double powerMax = 10.0;
	
	// What is the minimum player power?
	// NOTE: Negative minimum values is possible.
	public double powerMin = 0.0;
	
	// How much power should be regained per hour online on the server?
	public double powerPerHour = 2.0;
	
	// How much power should be lost on death?
	public double powerPerDeath = -2.0;
	
	// Can players with negative power leave their faction?
	// NOTE: This only makes sense to set to false if your "powerMin" setting is negative.
	public boolean canLeaveWithNegativePower = true;
	
	// -------------------------------------------- //
	// CORE
	// -------------------------------------------- //

	// Is there a maximum amount of members per faction?
	// 0 means there is not. If you set it to 100 then there can at most be 100 members per faction.
	public int factionMemberLimit = 0;
	
	// Is there a maximum faction power cap?
	// 0 means there is not. Set it to a positive value in case you wan't to use this feature.
	public double factionPowerMax = 0.0;
	
	// Limit the length of faction names here.
	public int factionNameLengthMin = 3;
	public int factionNameLengthMax = 16;
	
	// Should faction names automatically be converted to upper case?
	// You probably don't want this feature.
	// It's a remnant from old faction versions.
	public boolean factionNameForceUpperCase = false;
	
	// -------------------------------------------- //
	// SET LIMITS
	// -------------------------------------------- //
	
	// When using radius setting of faction territory, what is the maximum radius allowed?
	public int setRadiusMax = 30;
	
	// When using fill setting of faction territory, what is the maximum chunk count allowed?
	public int setFillMax = 1000;
	
	// -------------------------------------------- //
	// CLAIMS
	// -------------------------------------------- //
	
	// Must claims be connected to each other?
	// If you set this to false you will allow factions to claim more than one base per world map.
	// That would makes outposts possible but also potentially ugly weird claims messing up your Dynmap and ingame experiance.
	public boolean claimsMustBeConnected = true;
	
	// Would you like to allow unconnected claims when conquering land from another faction?
	// Setting this to true would allow taking over someone elses base even if claims normally have to be connected.
	// Note that even without this you can pillage/unclaim another factions territory in war.
	// You just won't be able to take the land as your own.
	public boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = false;
	
	// Is claiming from other factions even allowed?
	// Set this to false to disable territorial warfare altogether.
	public boolean claimingFromOthersAllowed = true;
	
	// Is a minimum distance (measured in chunks) to other factions required?
	// 0 means the feature is disabled.
	// Set the feature to 10 and there must be 10 chunks of wilderness between factions.
	// Factions may optionally allow their allies to bypass this limit by configuring their faction permissions ingame themselves.
	public int claimMinimumChunksDistanceToOthers = 0;
	
	// Do you need a minimum amount of faction members to claim land?
	// 1 means just the faction leader alone is enough.
	public int claimsRequireMinFactionMembers = 1;
	
	// Is there a maximum limit to chunks claimed?
	// 0 means there isn't.
	public int claimedLandsMax = 0;
	
	// -------------------------------------------- //
	// HOMES
	// -------------------------------------------- //
	
	// Is the home feature enabled?
	// If you set this to false players can't set homes or teleport home.
	public boolean homesEnabled = true;
	
	// Must homes be located inside the faction's territory?
	// It's usually a wise idea keeping this true.
	// Otherwise players can set their homes inside enemy territory.
	public boolean homesMustBeInClaimedTerritory = true;
	
	// Is the home teleport command available?
	// One reason you might set this to false is if you only want players going home on respawn after death.
	public boolean homesTeleportCommandEnabled = true;
	
	// These options can be used to limit rights to tp home under different circumstances.
	public boolean homesTeleportAllowedFromEnemyTerritory = true;
	public boolean homesTeleportAllowedFromDifferentWorld = true;
	public double homesTeleportAllowedEnemyDistance = 32.0;
	public boolean homesTeleportIgnoreEnemiesIfInOwnTerritory = true;
	
	// Should players teleport to faction home on death?
	// Set this to true to override the default respawn location.
	public boolean homesTeleportToOnDeathActive = false;
	
	// This value can be used to tweak compatibility with other plugins altering the respawn location.
	// Choose between: LOWEST, LOW, NORMAL, HIGH, HIGHEST and MONITOR.
	public EventPriority homesTeleportToOnDeathPriority = EventPriority.NORMAL;
	
	// -------------------------------------------- //
	// TERRITORY INFO
	// -------------------------------------------- //
	
	public boolean territoryInfoTitlesDefault = true;

	public String territoryInfoTitlesMain = "{relcolor}{name}";
	public String territoryInfoTitlesSub = "<i>{desc}";
	public int territoryInfoTitlesTicksIn = 5;
	public int territoryInfoTitlesTicksStay = 60;
	public int territoryInfoTitleTicksOut = 5;

	public String territoryInfoChat = "<i> ~ {relcolor}{name} <i>{desc}";
	
	// -------------------------------------------- //
	// ASSORTED
	// -------------------------------------------- //
	
	// Set this to true if want to block the promotion of new leaders for permanent factions.
	// I don't really understand the user case for this option.
	public boolean permanentFactionsDisableLeaderPromotion = false;
	
	// How much health damage should a player take upon placing or breaking a block in a "pain build" territory?
	// 2.0 means one heart.
	public double actionDeniedPainAmount = 2.0D;
	
	// If you set this option to true then factionless players cant partake in PVP.
	// It works in both directions. Meaning you must join a faction to hurt players and get hurt by players.
	public boolean disablePVPForFactionlessPlayers = false;
	
	// Set this option to true to create an exception to the rule above.
	// Players inside their own faction territory can then hurt facitonless players.
	// This way you may "evict" factionless trolls messing around in your home base.
	public boolean enablePVPAgainstFactionlessInAttackersLand = false;
	
	// Inside your own faction territory you take less damage.
	// 0.1 means that you take 10% less damage at home.
	public double territoryShieldFactor = 0.1D;
	
	// Protects the faction land from piston extending/retracting
	// through the denying of MPerm build
	public boolean handlePistonProtectionThroughDenyBuild = true;
	
	// -------------------------------------------- //
	// DENY COMMANDS
	// -------------------------------------------- //
	
	// A list of commands to block for members of permanent factions.
	// I don't really understand the user case for this option.
	public List<String> denyCommandsPermanentFactionMember = new ArrayList<String>();

	// Lists of commands to deny depending on your relation to the current faction territory.
	// You may for example not type /home (might be the plugin Essentials) in the territory of your enemies.
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
	
	// Should Factions set the chat format?
	// This should be kept at false if you use an external chat format plugin.
	// If you are planning on running a more lightweight server you can set this to true.
	public boolean chatSetFormat = true;
	
	// At which event priority should the chat format be set in such case?
	// Choose between: LOWEST, LOW, NORMAL, HIGH and HIGHEST.
	public EventPriority chatSetFormatAt = EventPriority.LOWEST;
	
	// What format should be set?
	public String chatSetFormatTo = "<{factions_relcolor}§l{factions_roleprefix}§r{factions_relcolor}{factions_name|rp}§f%1$s> %2$s";
	
	// Should the chat tags such as {factions_name} be parsed?
	// NOTE: You can set this to true even with chatSetFormat = false.
	// But in such case you must set the chat format using an external chat format plugin.
	public boolean chatParseTags = true;
	
	// At which event priority should the faction chat tags be parsed in such case?
	// Choose between: LOWEST, LOW, NORMAL, HIGH, HIGHEST.
	public EventPriority chatParseTagsAt = EventPriority.LOW;
	
	// -------------------------------------------- //
	// COLORS
	// -------------------------------------------- //
	
	// Here you can alter the colors tied to certain faction relations and settings.
	// You probably don't want to edit these to much.
	// Doing so might confuse players that are used to Factions.
	public ChatColor colorMember = ChatColor.GREEN;
	public ChatColor colorAlly = ChatColor.DARK_PURPLE;
	public ChatColor colorTruce = ChatColor.LIGHT_PURPLE;
	public ChatColor colorNeutral = ChatColor.WHITE;
	public ChatColor colorEnemy = ChatColor.RED;
	
	// This one is for example applied to SafeZone since that faction has the pvp flag set to false.
	public ChatColor colorNoPVP = ChatColor.GOLD;
	
	// This one is for example applied to WarZone since that faction has the friendly fire flag set to true.
	public ChatColor colorFriendlyFire = ChatColor.DARK_RED;
	
	// -------------------------------------------- //
	// PREFIXES
	// -------------------------------------------- //
	
	// Here you may edit the name prefixes associated with different faction ranks.
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
	public boolean handleNetherPortalTrap = true;
	
	// -------------------------------------------- //
	// SEE CHUNK
	// -------------------------------------------- //
	
	// These options can be used to tweak the "/f seechunk" particle effect.
	// They are fine as is but feel free to experiment with them if you want to.
	
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
	// UNSTUCK
	// -------------------------------------------- //
	
	public int unstuckSeconds = 30;
	public int unstuckChunkRadius = 10; 
	
	// -------------------------------------------- //
	// LOGGING
	// -------------------------------------------- //
	
	// Here you can disable logging of certain events to the server console.
	
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
	
	// These values are fine for most standard bukkit/spigot servers.
	// If you however are using Forge with mods that add new container types you might want to add them here.
	// This way they can be protected in Faction territory.
	
	// Interacting with these materials when they are already placed in the terrain results in an edit.
	public BackstringEnumSet<Material> materialsEditOnInteract = new BackstringEnumSet<Material>(Material.class,
		"DIODE_BLOCK_OFF", // Minecraft 1.?
		"DIODE_BLOCK_ON", // Minecraft 1.?
		"NOTE_BLOCK", // Minecraft 1.?
		"CAULDRON", // Minecraft 1.?
		"SOIL" // Minecraft 1.?
	);
	
	// Interacting with the the terrain holding this item in hand results in an edit.
	// There's no need to add all block materials here. Only special items other than blocks.
	public BackstringEnumSet<Material> materialsEditTools = new BackstringEnumSet<Material>(Material.class,
		"FIREBALL", // Minecraft 1.?
		"FLINT_AND_STEEL", // Minecraft 1.?
		"BUCKET", // Minecraft 1.?
		"WATER_BUCKET", // Minecraft 1.?
		"LAVA_BUCKET", // Minecraft 1.?
		"ARMOR_STAND" // Minecraft 1.8
	);
	
	// The duplication bug found in Spigot 1.8 protocol patch
	// https://github.com/MassiveCraft/Factions/issues/693
	public BackstringEnumSet<Material> materialsEditToolsDupeBug = new BackstringEnumSet<Material>(Material.class,
		"CHEST", // Minecraft 1.?
		"SIGN_POST", // Minecraft 1.?
		"TRAPPED_CHEST", // Minecraft 1.?
		"SIGN", // Minecraft 1.?
		"WOOD_DOOR", // Minecraft 1.?
		"IRON_DOOR" // Minecraft 1.?
	);
	
	// Interacting with these materials placed in the terrain results in door toggling.
	public BackstringEnumSet<Material> materialsDoor = new BackstringEnumSet<Material>(Material.class,
		"WOODEN_DOOR", // Minecraft 1.?
		"ACACIA_DOOR", // Minecraft 1.8
		"BIRCH_DOOR", // Minecraft 1.8
		"DARK_OAK_DOOR", // Minecraft 1.8
		"JUNGLE_DOOR", // Minecraft 1.8
		"SPRUCE_DOOR", // Minecraft 1.8
		"TRAP_DOOR", // Minecraft 1.?
		"FENCE_GATE", // Minecraft 1.?
		"ACACIA_FENCE_GATE", // Minecraft 1.8
		"BIRCH_FENCE_GATE", // Minecraft 1.8
		"DARK_OAK_FENCE_GATE", // Minecraft 1.8
		"JUNGLE_FENCE_GATE", // Minecraft 1.8
		"SPRUCE_FENCE_GATE" // Minecraft 1.8
	);
	
	// Interacting with these materials placed in the terrain results in opening a container.
	public BackstringEnumSet<Material> materialsContainer = new BackstringEnumSet<Material>(Material.class,
		"DISPENSER", // Minecraft 1.?
		"CHEST", // Minecraft 1.?
		"FURNACE", // Minecraft 1.?
		"BURNING_FURNACE", // Minecraft 1.?
		"JUKEBOX", // Minecraft 1.?
		"BREWING_STAND", // Minecraft 1.?
		"ENCHANTMENT_TABLE", // Minecraft 1.?
		"ANVIL", // Minecraft 1.?
		"BEACON", // Minecraft 1.?
		"TRAPPED_CHEST", // Minecraft 1.?
		"HOPPER", // Minecraft 1.?
		"DROPPER" // Minecraft 1.?
	);
	
	// Interacting with these entities results in an edit.
	public BackstringEnumSet<EntityType> entityTypesEditOnInteract = new BackstringEnumSet<EntityType>(EntityType.class,
		"ITEM_FRAME", // Minecraft 1.?
		"ARMOR_STAND" // Minecraft 1.8
	);
	
	// Damaging these entities results in an edit.
	public BackstringEnumSet<EntityType> entityTypesEditOnDamage = new BackstringEnumSet<EntityType>(EntityType.class,
		"ITEM_FRAME", // Minecraft 1.?
		"ARMOR_STAND" // Minecraft 1.8
	);
	
	// Interacting with these entities results in opening a container.
	public BackstringEnumSet<EntityType> entityTypesContainer = new BackstringEnumSet<EntityType>(EntityType.class,
		"MINECART_CHEST", // Minecraft 1.?
		"MINECART_HOPPER" // Minecraft 1.?
	);
	
	// The complete list of entities considered to be monsters.
	public BackstringEnumSet<EntityType> entityTypesMonsters = new BackstringEnumSet<EntityType>(EntityType.class,
		"BLAZE", // Minecraft 1.?
		"CAVE_SPIDER", // Minecraft 1.?
		"CREEPER", // Minecraft 1.?
		"ENDERMAN", // Minecraft 1.?
		"ENDERMITE", // Minecraft 1.8
		"ENDER_DRAGON", // Minecraft 1.?
		"GUARDIAN", // Minecraft 1.8
		"GHAST", // Minecraft 1.?
		"GIANT", // Minecraft 1.?
		"MAGMA_CUBE", // Minecraft 1.?
		"PIG_ZOMBIE", // Minecraft 1.?
		"SILVERFISH", // Minecraft 1.?
		"SKELETON", // Minecraft 1.?
		"SLIME", // Minecraft 1.?
		"SPIDER", // Minecraft 1.?
		"WITCH", // Minecraft 1.?
		"WITHER", // Minecraft 1.?
		"ZOMBIE" // Minecraft 1.?
	);
	
	// List of entities considered to be animals.
	public BackstringEnumSet<EntityType> entityTypesAnimals = new BackstringEnumSet<EntityType>(EntityType.class,
		"CHICKEN", // Minecraft 1.?
		"COW", // Minecraft 1.?
		"HORSE", // Minecraft 1.?
		"MUSHROOM_COW", // Minecraft 1.?
		"OCELOT", // Minecraft 1.?
		"PIG", // Minecraft 1.?
		"RABBIT", // Minecraft 1.?
		"SHEEP", // Minecraft 1.?
		"SQUID" // Minecraft 1.?
	);
	
	// -------------------------------------------- //
	// INTEGRATION: HeroChat
	// -------------------------------------------- //
	
	// I you are using the chat plugin HeroChat Factions ship with built in integration.
	// The two channels Faction and Allies will be created.
	// Their data is actually stored right here in the factions config.
	// NOTE: HeroChat will create it's own database files for these two channels.
	// You should ignore those and edit the channel settings from here.
	// Those HeroChat channel database files aren't read for the Faction and Allies channels.
	
	// The Faction Channel
	public String herochatFactionName = "Faction";
	public String herochatFactionNick = "F";
	public String herochatFactionFormat = "{color}[&l{nick}&r{color} &l{factions_roleprefix}&r{color}{factions_title|rp}{sender}{color}] &f{msg}";
	public ChatColor herochatFactionColor = ChatColor.GREEN;
	public int herochatFactionDistance = 0;
	public boolean herochatFactionIsShortcutAllowed = false;
	public boolean herochatFactionCrossWorld = true;
	public boolean herochatFactionMuted = false;
	public Set<String> herochatFactionWorlds = new HashSet<String>();
	
	// The Allies Channel
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
	
	// Do you need faction build rights in the territory to create an LWC protection there?
	public boolean lwcMustHaveBuildRightsToCreate = true;
	
	// The config option above does not handle situations where a player creates an LWC protection in Faction territory and then leaves the faction.
	// The player would then have an LWC protection in a territory where they can not build.
	// Set this config option to true to enable an automatic removal feature.
	// LWC protections that couldn't be created will be removed on an attempt to open them by any player.
	public boolean lwcRemoveIfNoBuildRights = false;
	
	// WARN: Experimental and semi buggy.
	// If you change this to true: alien LWC protections will be removed upon using /f set.
	public Map<EventFactionsChunkChangeType, Boolean> lwcRemoveOnChange = MUtil.map(
		EventFactionsChunkChangeType.BUY, false, // when claiming from wilderness
		EventFactionsChunkChangeType.SELL, false, // when selling back to wilderness
		EventFactionsChunkChangeType.CONQUER, false, // when claiming from another player faction
		EventFactionsChunkChangeType.PILLAGE, false // when unclaiming (to wilderness) from another player faction
	);
	
	// -------------------------------------------- //
	// INTEGRATION: WorldGuard
	// -------------------------------------------- //
	
	// Global WorldGuard Integration Switch
	public boolean worldguardCheckEnabled = false;
	
	// Enable the WorldGuard check per-world 
	// Specify which worlds the WorldGuard Check can be used in
	public WorldExceptionSet worldguardCheckWorldsEnabled = new WorldExceptionSet();
	
	// -------------------------------------------- //
	// INTEGRATION: ECONOMY
	// -------------------------------------------- //
	
	// Should economy features be enabled?
	// This requires that you have the external plugin called "Vault" installed.
	public boolean econEnabled = true;
	
	// A money reward per chunk. This reward is divided among the players in the faction.
	// You set the time inbetween each reward almost at the top of this config file. (taskEconLandRewardMinutes)
	public double econLandReward = 0.00;
	
	// When paying a cost you may specify an account that should receive the money here.
	// Per default "" the money is just destroyed.
	public String econUniverseAccount = "";
	
	// What is the price per chunk when using /f set?
	public Map<EventFactionsChunkChangeType, Double> econChunkCost = MUtil.map(
		EventFactionsChunkChangeType.BUY, 1.0, // when claiming from wilderness
		EventFactionsChunkChangeType.SELL, 0.0, // when selling back to wilderness
		EventFactionsChunkChangeType.CONQUER, 0.0, // when claiming from another player faction
		EventFactionsChunkChangeType.PILLAGE, 0.0 // when unclaiming (to wilderness) from another player faction
	);
	
	// What is the price to create a faction?
	public double econCostCreate = 100.0;
	
	// And so on and so forth ... you get the idea.
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
	
	// Should the faction bank system be enabled?
	// This enables the command /f money.
	public boolean bankEnabled = true;
	
	// That costs should the faciton bank take care of?
	// If you set this to false the player executing the command will pay instead.
	public boolean bankFactionPaysCosts = true;
	public boolean bankFactionPaysLandCosts = true;

}
