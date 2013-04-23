package com.massivecraft.factions;

import com.massivecraft.factions.adapter.BoardAdapter;
import com.massivecraft.factions.adapter.BoardMapAdapter;
import com.massivecraft.factions.adapter.FFlagAdapter;
import com.massivecraft.factions.adapter.FPermAdapter;
import com.massivecraft.factions.adapter.RelAdapter;
import com.massivecraft.factions.adapter.TerritoryAccessAdapter;
import com.massivecraft.factions.chat.modifier.ChatModifierLc;
import com.massivecraft.factions.chat.modifier.ChatModifierLp;
import com.massivecraft.factions.chat.modifier.ChatModifierParse;
import com.massivecraft.factions.chat.modifier.ChatModifierRp;
import com.massivecraft.factions.chat.modifier.ChatModifierUc;
import com.massivecraft.factions.chat.modifier.ChatModifierUcf;
import com.massivecraft.factions.chat.tag.ChatTagRelcolor;
import com.massivecraft.factions.chat.tag.ChatTagRole;
import com.massivecraft.factions.chat.tag.ChatTagRoleprefix;
import com.massivecraft.factions.chat.tag.ChatTagTag;
import com.massivecraft.factions.chat.tag.ChatTagTagforce;
import com.massivecraft.factions.chat.tag.ChatTagTitle;
import com.massivecraft.factions.cmd.*;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.UPlayerColls;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.MConfColl;
import com.massivecraft.factions.integration.LWCFeatures;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.integration.herochat.HerochatFeatures;
import com.massivecraft.factions.listeners.FactionsListenerChat;
import com.massivecraft.factions.listeners.FactionsListenerEcon;
import com.massivecraft.factions.listeners.FactionsListenerExploit;
import com.massivecraft.factions.listeners.FactionsListenerMain;
import com.massivecraft.factions.listeners.TodoFactionsPlayerListener;
import com.massivecraft.factions.task.AutoLeaveTask;
import com.massivecraft.factions.task.EconLandRewardTask;

import com.massivecraft.mcore.MPlugin;
import com.massivecraft.mcore.usys.Aspect;
import com.massivecraft.mcore.usys.AspectColl;
import com.massivecraft.mcore.usys.Multiverse;
import com.massivecraft.mcore.util.MUtil;
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

	// Aspects
	private Aspect aspect;
	public Aspect getAspect() { return this.aspect; }
	public Multiverse getMultiverse() { return this.getAspect().getMultiverse(); }

	// Database Initialized
	private boolean databaseInitialized;
	public boolean isDatabaseInitialized() { return this.databaseInitialized; }

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void onEnable()
	{
		if ( ! preEnable()) return;

		// Load Server Config
		ConfServer.get().load();

		// Initialize Aspects
		this.aspect = AspectColl.get().get(Const.ASPECT_ID, true);
		this.aspect.register();
		this.aspect.setDesc(
			"<i>If the factions system even is enabled and how it's configured.",
			"<i>What factions exists and what players belong to them."
		);

		// Register Faction accountId Extractor
		// TODO: Perhaps this should be placed in the econ integration somewhere?
		MUtil.registerExtractor(String.class, "accountId", ExtractorFactionAccountId.get());

		// Initialize Collections
		this.databaseInitialized = false;

		MConfColl.get().init();
		UPlayerColls.get().init();
		FactionColls.get().init();
		BoardColls.get().init();

		FactionColls.get().reindexUPlayers();

		this.databaseInitialized = true;

		// Commands
		this.outerCmdFactions = new CmdFactions();
		this.outerCmdFactions.register(this);

		// Setup Listeners
		FactionsListenerMain.get().setup();
		FactionsListenerChat.get().setup();
		FactionsListenerExploit.get().setup();

		// TODO: This listener is a work in progress.
		// The goal is that the Econ integration should be completely based on listening to our own events.
		// Right now only a few situations are handled through this listener.
		FactionsListenerEcon.get().setup();

		// TODO: Get rid of this one
		this.playerListener = new TodoFactionsPlayerListener();
		getServer().getPluginManager().registerEvents(this.playerListener, this);

		// Schedule recurring non-tps-dependent tasks
		AutoLeaveTask.get().schedule(this);
		EconLandRewardTask.get().schedule(this);

		// Register built in chat modifiers
		ChatModifierLc.get().register();
		ChatModifierLp.get().register();
		ChatModifierParse.get().register();
		ChatModifierRp.get().register();
		ChatModifierUc.get().register();
		ChatModifierUcf.get().register();

		// Register built in chat tags
		ChatTagRelcolor.get().register();
		ChatTagRole.get().register();
		ChatTagRoleprefix.get().register();
		ChatTagTag.get().register();
		ChatTagTagforce.get().register();
		ChatTagTitle.get().register();

		// Integrate
		this.integrate(HerochatFeatures.get());

		LWCFeatures.setup();

		if (ConfServer.worldGuardChecking)
		{
			Worldguard.init(this);
		}

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
