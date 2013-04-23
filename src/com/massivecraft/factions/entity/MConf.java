package com.massivecraft.factions.entity;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.event.EventPriority;

import com.massivecraft.mcore.store.Entity;

public class MConf extends Entity<MConf>
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //

	protected static transient MConf i;
	public static MConf get() { return i; }

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
	// CHAT
	// -------------------------------------------- //

	// We offer a simple standard way to set the format
	public boolean chatSetFormat = false;
	public EventPriority chatSetFormatAt = EventPriority.LOWEST;
	public String chatSetFormatTo = "<{factions_relcolor}§l{factions_roleprefix}§r{factions_relcolor}{factions_tag|rp}§f%1$s> %2$s";

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
	public String herochatAlliesFormat = "{color}[&l{nick}&r&f {factions_relcolor}&l{factions_roleprefix}&r{factions_relcolor}{factions_tag|rp}{sender}{color}] &f{msg}";
	public ChatColor herochatAlliesColor = ChatColor.DARK_PURPLE;
	public int herochatAlliesDistance = 0;
	public boolean herochatAlliesIsShortcutAllowed = false;
	public boolean herochatAlliesCrossWorld = true;
	public boolean herochatAlliesMuted = false;
	public Set<String> herochatAlliesWorlds = new HashSet<String>();

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
	// EXPLOITS
	// -------------------------------------------- //

	public boolean handleExploitObsidianGenerators = true;
	public boolean handleExploitEnderPearlClipping = true;
	public boolean handleExploitInteractionSpam = true;
	public boolean handleExploitTNTWaterlog = false;

	// -------------------------------------------- //
	// DERPY OVERRIDES
	// -------------------------------------------- //
	// TODO: Should worldsNoPowerLoss rather be a bukkit permission node?
	// TODO: These are derpy because they possibly use an invalid design approact.
	// After universe support is added. Would some of these be removed?
	// Could it also be more customizeable using some sort of permission lookup map?

	// mainly for other plugins/mods that use a fake player to take actions, which shouldn't be subject to our protections
	public Set<String> playersWhoBypassAllProtection = new LinkedHashSet<String>();

	public Set<String> worldsNoClaiming = new LinkedHashSet<String>();
	public Set<String> worldsNoPowerLoss = new LinkedHashSet<String>();
	public Set<String> worldsIgnorePvP = new LinkedHashSet<String>();

}
