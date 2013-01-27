package com.massivecraft.factions;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.Location;
import org.bukkit.Material;

import com.massivecraft.factions.adapters.FFlagTypeAdapter;
import com.massivecraft.factions.adapters.FPermTypeAdapter;
import com.massivecraft.factions.adapters.LocationTypeAdapter;
import com.massivecraft.factions.adapters.RelTypeAdapter;
import com.massivecraft.factions.cmd.*;
import com.massivecraft.factions.integration.capi.CapiFeatures;
import com.massivecraft.factions.integration.herochat.HerochatFeatures;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.EssentialsFeatures;
import com.massivecraft.factions.integration.LWCFeatures;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.listeners.FactionsChatListener;
import com.massivecraft.factions.listeners.FactionsEntityListener;
import com.massivecraft.factions.listeners.FactionsExploitListener;
import com.massivecraft.factions.listeners.FactionsAppearanceListener;
import com.massivecraft.factions.listeners.FactionsPlayerListener;
import com.massivecraft.factions.listeners.FactionsServerListener;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.struct.TerritoryAccess;
import com.massivecraft.factions.util.AutoLeaveTask;
import com.massivecraft.factions.util.EconLandRewardTask;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.zcore.MPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;


public class P extends MPlugin
{
	// Our single plugin instance
	public static P p;
	
	// Listeners
	public final FactionsPlayerListener playerListener;
	public final FactionsChatListener chatListener;
	public final FactionsEntityListener entityListener;
	public final FactionsExploitListener exploitListener;
	public final FactionsBlockListener blockListener;
	public final FactionsServerListener serverListener;
	public final FactionsAppearanceListener appearanceListener;
	
	// Persistance related
	private boolean locked = false;
	public boolean getLocked() {return this.locked;}
	public void setLocked(boolean val) {this.locked = val; this.setAutoSave(val);}
	private Integer AutoLeaveTask = null;
	private Integer econLandRewardTaskID = null;
	
	// Commands
	public FCmdRoot cmdBase;
	public CmdAutoHelp cmdAutoHelp;
	
	public P()
	{
		p = this;
		this.playerListener = new FactionsPlayerListener(this);
		this.chatListener = new FactionsChatListener(this);
		this.entityListener = new FactionsEntityListener(this);
		this.exploitListener = new FactionsExploitListener();
		this.blockListener = new FactionsBlockListener(this);
		this.serverListener = new FactionsServerListener(this);
		this.appearanceListener = new FactionsAppearanceListener(this);
	}

	@Override
	public void onEnable()
	{
		// bit of (apparently absolutely necessary) idiot-proofing for CB version support due to changed GSON lib package name
		try
		{
			Class.forName("org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken");
		}
		catch (ClassNotFoundException ex)
		{
			this.log(Level.SEVERE, "GSON lib not found. Your CraftBukkit build is too old (< 1.3.2) or otherwise not compatible.");
			this.suicide();
			return;
		}

		if ( ! preEnable()) return;
		this.loadSuccessful = false;

		// Load Conf from disk
		Conf.load();
		FPlayers.i.loadFromDisc();
		Factions.i.loadFromDisc();
		Board.load();
		
		// Add Base Commands
		this.cmdAutoHelp = new CmdAutoHelp();
		this.cmdBase = new FCmdRoot();

		EssentialsFeatures.setup();
		SpoutFeatures.setup();
		Econ.setup();
		CapiFeatures.setup();
		HerochatFeatures.setup();
		LWCFeatures.setup();
		
		if(Conf.worldGuardChecking)
		{
			Worldguard.init(this);
		}

		// start up task which runs the autoLeaveAfterDaysOfInactivity routine
		startAutoLeaveTask(false);

		// start up task which runs the econLandRewardRoutine
		startEconLandRewardTask(false);

		// Register Event Handlers
		getServer().getPluginManager().registerEvents(this.playerListener, this);
		getServer().getPluginManager().registerEvents(this.chatListener, this);
		getServer().getPluginManager().registerEvents(this.entityListener, this);
		getServer().getPluginManager().registerEvents(this.exploitListener, this);
		getServer().getPluginManager().registerEvents(this.blockListener, this);
		getServer().getPluginManager().registerEvents(this.serverListener, this);
		getServer().getPluginManager().registerEvents(this.appearanceListener, this);

		postEnable();
		this.loadSuccessful = true;
	}
	
	@Override
	public GsonBuilder getGsonBuilder()
	{
		return new GsonBuilder()
		.setPrettyPrinting()
		.disableHtmlEscaping()
		.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
		.registerTypeAdapter(LazyLocation.class, new LocationTypeAdapter())
		.registerTypeAdapter(TerritoryAccess.class, new TerritoryAccess())
		.registerTypeAdapter(Rel.class, new RelTypeAdapter())
		.registerTypeAdapter(FPerm.class, new FPermTypeAdapter())
		.registerTypeAdapter(FFlag.class, new FFlagTypeAdapter());
	}

	@Override
	public void onDisable()
	{
		// only save data if plugin actually completely loaded successfully
		if (this.loadSuccessful)
		{
			Board.save();
			Conf.save();
		}
		EssentialsFeatures.unhookChat();
		if (AutoLeaveTask != null)
		{
			this.getServer().getScheduler().cancelTask(AutoLeaveTask);
			AutoLeaveTask = null;
		}
		super.onDisable();
	}

	public void startAutoLeaveTask(boolean restartIfRunning)
	{
		if (AutoLeaveTask != null)
		{
			if ( ! restartIfRunning) return;
			this.getServer().getScheduler().cancelTask(AutoLeaveTask);
		}

		if (Conf.autoLeaveRoutineRunsEveryXMinutes > 0.0)
		{
			long ticks = (long)(20 * 60 * Conf.autoLeaveRoutineRunsEveryXMinutes);
			AutoLeaveTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoLeaveTask(), ticks, ticks);
		}
	}

	public void startEconLandRewardTask(boolean restartIfRunning)
	{
		if (econLandRewardTaskID != null)
		{
			if (!restartIfRunning) return;
			this.getServer().getScheduler().cancelTask(econLandRewardTaskID);
		}

		if (Conf.econEnabled &&
			Conf.econLandRewardTaskRunsEveryXMinutes > 0.0 &&
			Conf.econLandReward > 0.0)
		{
			long ticks = (long)(20 * 60 * Conf.econLandRewardTaskRunsEveryXMinutes);
			econLandRewardTaskID = getServer().getScheduler().scheduleSyncRepeatingTask(this, new EconLandRewardTask(), ticks, ticks);
		}
	}

	@Override
	public void postAutoSave()
	{
		Board.save();
		Conf.save();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split)
	{
		this.cmdBase.execute(sender, new ArrayList<String>(Arrays.asList(split)));
		return true;
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
	
	
	public boolean shouldLetFactionsHandleThisChat(AsyncPlayerChatEvent event)
	{
		if (event == null) return false;
		return (isPlayerFactionChatting(event.getPlayer()) || isFactionsCommand(event.getMessage()));
	}

	// Does player have Faction Chat enabled? If so, chat plugins should preferably not do channels,
	// local chat, or anything else which targets individual recipients, so Faction Chat can be done
	/**
	 * @deprecated  As of release 1.8, there is no built in faction chat.
	 */
	public boolean isPlayerFactionChatting(Player player)
	{
		return false;
	}

	// Is this chat message actually a Factions command, and thus should be left alone by other plugins?
	/**
	 * @deprecated As of release 1.8.1 the normal Bukkit command-handling is used. 
	 */
	public boolean isFactionsCommand(String check)
	{
		return false;
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
		if (listener == null || !Conf.chatParseTagsColored) {
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
		Faction faction = Factions.i.getByTag(factionTag);
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
		Faction faction = Factions.i.getByTag(factionTag);
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
		return FactionsBlockListener.playerCanBuildDestroyBlock(player, location.getBlock(), "", true);
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
}
