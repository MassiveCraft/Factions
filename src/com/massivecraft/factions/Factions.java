package com.massivecraft.factions;

import com.massivecraft.factions.adapters.BoardAdapter;
import com.massivecraft.factions.adapters.BoardMapAdapter;
import com.massivecraft.factions.adapters.FFlagAdapter;
import com.massivecraft.factions.adapters.FPermAdapter;
import com.massivecraft.factions.adapters.RelAdapter;
import com.massivecraft.factions.adapters.TerritoryAccessAdapter;
import com.massivecraft.factions.cmd.*;
import com.massivecraft.factions.integration.herochat.HerochatFeatures;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.LWCFeatures;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.listeners.FactionsListenerChat;
import com.massivecraft.factions.listeners.TodoFactionsEntityListener;
import com.massivecraft.factions.listeners.FactionsListenerExploit;
import com.massivecraft.factions.listeners.FactionsListenerMain;
import com.massivecraft.factions.listeners.TodoFactionsPlayerListener;
import com.massivecraft.factions.task.AutoLeaveTask;
import com.massivecraft.factions.task.EconLandRewardTask;

import com.massivecraft.mcore.MPlugin;
import com.massivecraft.mcore.xlib.gson.GsonBuilder;


public class Factions extends MPlugin
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static Factions i;
	public static Factions get() { return i; }
	public Factions() { Factions.i = this; }
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	// Commands
	private CmdFactions outerCmdFactions;
	public CmdFactions getOuterCmdFactions() { return this.outerCmdFactions; }
	
	// Listeners
	public TodoFactionsPlayerListener playerListener;
	public TodoFactionsEntityListener entityListener;

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void onEnable()
	{
		if ( ! preEnable()) return;
		
		// Load Server Config
		ConfServer.get().load();

		// Initialize Collections
		FPlayerColl.get().init();
		FactionColl.get().init();
		BoardColl.get().init();
		
		// Commands
		this.outerCmdFactions = new CmdFactions();
		this.outerCmdFactions.register(this);

		SpoutFeatures.setup();
		Econ.setup();
		HerochatFeatures.setup();
		LWCFeatures.setup();
		
		if (ConfServer.worldGuardChecking)
		{
			Worldguard.init(this);
		}

		// Schedule recurring non-tps-dependent tasks
		AutoLeaveTask.get().schedule(this);
		EconLandRewardTask.get().schedule(this);

		// Register Event Handlers
		FactionsListenerMain.get().setup();
		FactionsListenerChat.get().setup();
		FactionsListenerExploit.get().setup();
		
		// TODO: Get rid of these
		this.playerListener = new TodoFactionsPlayerListener();
		getServer().getPluginManager().registerEvents(this.playerListener, this);
		
		this.entityListener = new TodoFactionsEntityListener();
		getServer().getPluginManager().registerEvents(this.entityListener, this);
		
		postEnable();
	}
	
	@Override
	public GsonBuilder getGsonBuilder()
	{
		return super.getGsonBuilder()
		.registerTypeAdapter(TerritoryAccess.class, TerritoryAccessAdapter.get())
		.registerTypeAdapter(Board.class, BoardAdapter.get())
		.registerTypeAdapter(Board.MAP_TYPE, BoardMapAdapter.get())
		.registerTypeAdapter(Rel.class, RelAdapter.get())
		.registerTypeAdapter(FPerm.class, FPermAdapter.get())
		.registerTypeAdapter(FFlag.class, FFlagAdapter.get())
		;
	}

	// -------------------------------------------- //
	// Functions for other plugins to hook into
	// -------------------------------------------- //
	// TODO: This "outer API" is removed. I should ensure these features are
	// available using the appropriate classes and then remove this commented out section below.
	
	/*

	// This value will be updated whenever new hooks are added
	public int hookSupportVersion()
	{
		return 3;
	}

	// If another plugin is handling insertion of chat tags, this should be used to notify Factions
	public void handleFactionTagExternally(boolean notByFactions)
	{
		ConfServer.chatTagHandledByAnotherPlugin = notByFactions;
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

		FPlayer me = FPlayerColl.i.get(speaker);
		if (me == null)
			return tag;

		// if listener isn't set, or config option is disabled, give back uncolored tag
		if (listener == null || !ConfServer.chatParseTagsColored) {
			tag = me.getChatTag().trim();
		} else {
			FPlayer you = FPlayerColl.i.get(listener);
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

		FPlayer me = FPlayerColl.i.get(player);
		if (me == null)
			return "";

		return me.getTitle().trim();
	}

	// Get a list of all faction tags (names)
	public Set<String> getFactionTags()
	{
		Set<String> tags = new HashSet<String>();
		for (Faction faction : FactionColl.i.get())
		{
			tags.add(faction.getTag());
		}
		return tags;
	}

	// Get a list of all players in the specified faction
	public Set<String> getPlayersInFaction(String factionTag)
	{
		Set<String> players = new HashSet<String>();
		Faction faction = FactionColl.i.getByTag(factionTag);
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
		Faction faction = FactionColl.i.getByTag(factionTag);
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
	
	*/
	
}
