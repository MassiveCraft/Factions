package com.massivecraft.factions.entity;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;

import com.massivecraft.factions.Factions;
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
	// CLAIM LIMITS
	// -------------------------------------------- //
	
	// if someone is doing a radius claim and the process fails to claim land this many times in a row, it will exit
	public int radiusClaimFailureLimit = 9;
	
	// the maximum radius allowed when using the claim command.
	public int radiusClaimRadiusLimit = 5;
	
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

}