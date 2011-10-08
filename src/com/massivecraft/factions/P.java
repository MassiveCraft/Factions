package com.massivecraft.factions;

import java.io.File;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.massivecraft.factions.commands.*;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.listeners.FactionsChatEarlyListener;
import com.massivecraft.factions.listeners.FactionsEntityListener;
import com.massivecraft.factions.listeners.FactionsPlayerListener;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.util.MapFLocToStringSetTypeAdapter;
import com.massivecraft.factions.util.MyLocationTypeAdapter;
import com.massivecraft.factions.zcore.MPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.earth2me.essentials.chat.EssentialsChat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.integration.EssentialsFeatures;

/**
 * The data is saved to disk every 30min and on plugin disable.
 */
public class P extends MPlugin
{
	// Our single plugin instance
	public static P p;
	
	// Listeners
	public final FactionsPlayerListener playerListener;
	public final FactionsChatEarlyListener chatEarlyListener;
	public final FactionsEntityListener entityListener;
	public final FactionsBlockListener blockListener;
	
	public P()
	{
		p = this;
		this.playerListener = new FactionsPlayerListener(this);
		this.chatEarlyListener = new FactionsChatEarlyListener(this);
		this.entityListener = new FactionsEntityListener(this);
		this.blockListener = new FactionsBlockListener(this);
	}
	
	public static PermissionHandler Permissions;
	private static EssentialsChat essChat;
	
	
	@Override
	public void onEnable()
	{
		if ( ! preEnable()) return;
		
		// Load Conf from disk
		Conf.load();
		FPlayers.i.loadFromDisc();
		Factions.i.loadFromDisc();
		Board.load();
		
		//setupPermissions();
		integrateEssentialsChat();
		setupSpout(this);
		Econ.setup(this);
		Econ.monitorPlugins();
		
		if(Conf.worldGuardChecking)
		{
			Worldguard.init(this);			
		}
		
		Type mapFLocToStringSetType = new TypeToken<Map<FLocation, Set<String>>>(){}.getType();
		
		// Add the commands
		commands.add(new FCommandHelp());
		commands.add(new FCommandAdmin());
		commands.add(new FCommandAutoClaim());
		commands.add(new FCommandAutoSafeclaim());
		commands.add(new FCommandAutoWarclaim());
		commands.add(new FCommandBalance());
		commands.add(new FCommandBypass());
		commands.add(new FCommandChat());
		commands.add(new FCommandClaim());
		commands.add(new FCommandConfig());
		commands.add(new FCommandCreate());
		commands.add(new FCommandDeinvite());
		commands.add(new FCommandDeposit());
		commands.add(new FCommandDescription());
		commands.add(new FCommandDisband());
		commands.add(new FCommandHome());
		commands.add(new FCommandInvite());
		commands.add(new FCommandJoin());
		commands.add(new FCommandKick());
		commands.add(new FCommandLeave());
		commands.add(new FCommandList());
		commands.add(new FCommandLock());
		commands.add(new FCommandMap());
		commands.add(new FCommandMod());
		commands.add(new FCommandNoBoom());
		commands.add(new FCommandOpen());
		commands.add(new FCommandOwner());
		commands.add(new FCommandOwnerList());
		commands.add(new FCommandPay());
		commands.add(new FCommandPower());
		commands.add(new FCommandPeaceful());
		commands.add(new FCommandPermanent());
		commands.add(new FCommandRelationAlly());
		commands.add(new FCommandRelationEnemy());
		commands.add(new FCommandRelationNeutral());
		commands.add(new FCommandReload());
		commands.add(new FCommandSafeclaim());
		commands.add(new FCommandSafeunclaimall());
		commands.add(new FCommandSaveAll());
		commands.add(new FCommandSethome());
		commands.add(new FCommandShow());
		commands.add(new FCommandTag());
		commands.add(new FCommandTitle());
		commands.add(new FCommandUnclaim());
		commands.add(new FCommandUnclaimall());
		commands.add(new FCommandVersion());
		commands.add(new FCommandWarclaim());
		commands.add(new FCommandWarunclaimall());
		commands.add(new FCommandWithdraw());
		
		// Register events
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.chatEarlyListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, this.playerListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_FILL, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENDERMAN_PICKUP, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENDERMAN_PLACE, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.CREATURE_SPAWN, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_TARGET, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PAINTING_BREAK, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PAINTING_PLACE, this.entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGE, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PISTON_EXTEND, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PISTON_RETRACT, this.blockListener, Event.Priority.Normal, this);
		
		// Register recurring tasks
		/*long saveTicks = 20 * 60 * 30; // Approximately every 30 min
		if (saveTask == null)
			saveTask = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new SaveTask(), saveTicks, saveTicks);
		*/
		postEnable();
	}
	
	@Override
	public GsonBuilder getGsonBuilder()
	{
		return new GsonBuilder()
		.setPrettyPrinting()
		.disableHtmlEscaping()
		.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
	}

	@Override
	public void onDisable()
	{
		unhookEssentialsChat();
		super.onDisable();
	}

	// -------------------------------------------- //
	// Integration with other plugins
	// -------------------------------------------- //

	private void setupSpout(P factions)
	{
		Plugin test = factions.getServer().getPluginManager().getPlugin("Spout");

		if (test != null && test.isEnabled())
		{
			SpoutFeatures.setAvailable(true, test.getDescription().getFullName());
		}
	}

	private void integrateEssentialsChat()
	{
		if (essChat != null) return;

		Plugin test = this.getServer().getPluginManager().getPlugin("EssentialsChat");

		if (test != null && test.isEnabled())
		{
			essChat = (EssentialsChat)test;
			EssentialsFeatures.integrateChat(essChat);
		}
	}
	
	private void unhookEssentialsChat()
	{
		if (essChat != null) {
			EssentialsFeatures.unhookChat();
		}
	}

	// -------------------------------------------- //
	// Functions for other plugins to hook into
	// -------------------------------------------- //

	// This value will be updated whenever new hooks are added
	public int hookSupportVersion()
	{
		return 3;
	}

	// If another plugin is handling insertion of chat tags, this should be used to notify Factions
	public void handleFactionTagExternally(boolean notByFactions)
	{
		Conf.chatTagHandledByAnotherPlugin = notByFactions;
	}

	// Simply put, should this chat event be left for Factions to handle? For now, that means players with Faction Chat
	// enabled or use of the Factions f command without a slash; combination of isPlayerFactionChatting() and isFactionsCommand()
	public boolean shouldLetFactionsHandleThisChat(PlayerChatEvent event)
	{
		if (event == null) return false;
		return (isPlayerFactionChatting(event.getPlayer()) || isFactionsCommand(event.getMessage()));
	}

	// Does player have Faction Chat enabled? If so, chat plugins should preferably not do channels,
	// local chat, or anything else which targets individual recipients, so Faction Chat can be done
	public boolean isPlayerFactionChatting(Player player)
	{
		if (player == null) return false;
		FPlayer me = FPlayers.i.get(player);
		
		if (me == null)return false;
		return me.getChatMode().isAtLeast(ChatMode.ALLIANCE);
	}

	// Is this chat message actually a Factions command, and thus should be left alone by other plugins?
	public boolean isFactionsCommand(String check)
	{
		if (check == null || check.isEmpty()) return false;
		return (Conf.allowNoSlashCommand && (check.startsWith(p.getBaseCommand()+" ") || check.equals(p.getBaseCommand())));
	}

	// Get a player's faction tag (faction name), mainly for usage by chat plugins for local/channel chat
	public String getPlayerFactionTag(Player player)
	{
		return getPlayerFactionTagRelation(player, null);
	}

	// Same as above, but with relation (enemy/neutral/ally) coloring potentially added to the tag
	public String getPlayerFactionTagRelation(Player speaker, Player listener)
	{
		String tag = "~";

		if (speaker == null)
			return tag;

		FPlayer me = FPlayers.i.get(speaker);
		if (me == null)
			return tag;

		// if listener isn't set, or config option is disabled, give back uncolored tag
		if (listener == null || !Conf.chatTagRelationColored) {
			tag = me.getChatTag().trim();
		} else {
			FPlayer you = FPlayers.i.get(listener);
			if (you == null)
				tag = me.getChatTag().trim();
			else  // everything checks out, give the colored tag
				tag = me.getChatTag(you).trim();
		}
		if (tag.isEmpty())
			tag = "~";

		return tag;
	}

	// Get a player's title within their faction, mainly for usage by chat plugins for local/channel chat
	public String getPlayerTitle(Player player)
	{
		if (player == null)
			return "";

		FPlayer me = FPlayers.i.get(player);
		if (me == null)
			return "";

		return me.getTitle().trim();
	}

	// Get a list of all faction tags (names)
	public Set<String> getFactionTags()
	{
		Set<String> tags = new HashSet<String>();
		for (Faction faction : Factions.i.get())
		{
			tags.add(faction.getTag());
		}
		return tags;
	}

	// Get a list of all players in the specified faction
	public Set<String> getPlayersInFaction(String factionTag)
	{
		Set<String> players = new HashSet<String>();
		Faction faction = Factions.i.findByTag(factionTag);
		if (faction != null)
		{
			for (FPlayer fplayer : faction.getFPlayers())
			{
				players.add(fplayer.getName());
			}
		}
		return players;
	}

	// Get a list of all online players in the specified faction
	public Set<String> getOnlinePlayersInFaction(String factionTag)
	{
		Set<String> players = new HashSet<String>();
		Faction faction = Factions.i.findByTag(factionTag);
		if (faction != null)
		{
			for (FPlayer fplayer : faction.getFPlayersWhereOnline(true))
			{
				players.add(fplayer.getName());
			}
		}
		return players;
	}

	// check if player is allowed to build/destroy in a particular location
	public boolean isPlayerAllowedToBuildHere(Player player, Location location)
	{
		return FactionsBlockListener.playerCanBuildDestroyBlock(player, location, "", true);
	}

	// check if player is allowed to interact with the specified block (doors/chests/whatever)
	public boolean isPlayerAllowedToInteractWith(Player player, Block block)
	{
		return FactionsPlayerListener.canPlayerUseBlock(player, block, true);
	}

	// check if player is allowed to use a specified item (flint&steel, buckets, etc) in a particular location
	public boolean isPlayerAllowedToUseThisHere(Player player, Location location, Material material)
	{
		return FactionsPlayerListener.playerCanUseItemHere(player, location, material, true);
	}

	// -------------------------------------------- //
	// Test rights
	// -------------------------------------------- //
	
	public static boolean hasPermParticipate(CommandSender sender) {
		return hasPerm(sender, "factions.participate");
	}
	
	public static boolean hasPermCreate(CommandSender sender) {
		return hasPerm(sender, "factions.create");
	}
	
	public static boolean hasPermManageSafeZone(CommandSender sender) {
		return hasPerm(sender, "factions.manageSafeZone");
	}
	
	public static boolean hasPermManageWarZone(CommandSender sender) {
		return hasPerm(sender, "factions.manageWarZone");
	}

	public static boolean hasPermAdminBypass(CommandSender sender) {
		return hasPerm(sender, "factions.adminBypass");
	}
	
	public static boolean hasPermReload(CommandSender sender) {
		return hasPerm(sender, "factions.reload");
	}
	
	public static boolean hasPermSaveAll(CommandSender sender) {
		return hasPerm(sender, "factions.saveall");
	}
	
	public static boolean hasPermLock(CommandSender sender) {
		return hasPerm(sender, "factions.lock");
	}
	
	public static boolean hasPermConfigure(CommandSender sender) {
		return hasPerm(sender, "factions.config");
	}
	
	public static boolean hasPermDisband(CommandSender sender) {
		return hasPerm(sender, "factions.disband");
	}
	
	public static boolean hasPermViewAnyPower(CommandSender sender) {
		return hasPerm(sender, "factions.viewAnyPower");
	}
	
	public static boolean hasPermOwnershipBypass(CommandSender sender) {
		return hasPerm(sender, "factions.ownershipBypass");
	}
	
	public static boolean hasPermSetPeaceful(CommandSender sender) {
		return hasPerm(sender, "factions.setPeaceful");
	}
	
	public static boolean hasPermSetPermanent(CommandSender sender) {
		return hasPerm(sender, "factions.setPermanent");
	}
	
	public static boolean hasPermPeacefulExplosionToggle(CommandSender sender) {
		return hasPerm(sender, "factions.peacefulExplosionToggle");
	}
	
	public static boolean hasPermViewAnyFactionBalance(CommandSender sender) {
		return hasPerm(sender, "factions.viewAnyFactionBalance");
	}
	
	public static boolean isCommandDisabled(CommandSender sender, String command) {
		return (hasPerm(sender, "factions.commandDisable."+command) && !hasPerm(sender, "factions.commandDisable.none"));
	}
	
	private static boolean hasPerm(CommandSender sender, String permNode) {
		if (P.Permissions == null || ! (sender instanceof Player)) {
			return sender.isOp() || sender.hasPermission(permNode);
		}
		
		Player player = (Player)sender;
		return P.Permissions.has(player, permNode); 
	}
	
	// -------------------------------------------- //
	// Commands
	// -------------------------------------------- //
	/*
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		List<String> parameters = new ArrayList<String>(Arrays.asList(args));
		this.handleCommand(sender, parameters);
		return true;
	}
	
	public void handleCommand(CommandSender sender, List<String> parameters)
	{
		if (parameters.size() == 0)
		{
			this.commands.get(0).execute(sender, parameters);
			return;
		}
		
		String commandName = parameters.get(0).toLowerCase();
		parameters.remove(0);
		
		for (FBaseCommand fcommand : this.commands)
		{
			if (fcommand.getAliases().contains(commandName))
			{
				fcommand.execute(sender, parameters);
				return;
			}
		}
		
		sender.sendMessage(Conf.colorSystem+"Unknown faction command \""+commandName+"\". Try "+Conf.colorCommand+"/"+this.getBaseCommand()+" help");
	}
	*/
	// -------------------------------------------- //
	// Save all
	// -------------------------------------------- //
	
	// TODO: Add a hook to this??
	public static void saveAll()
	{
		// FPlayer.save();
		// Faction.save();
		Board.save();
		Conf.save();
	}
	
}
