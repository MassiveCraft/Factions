package com.massivecraft.factions;

import com.massivecraft.factions.adapter.BoardAdapter;
import com.massivecraft.factions.adapter.BoardMapAdapter;
import com.massivecraft.factions.adapter.FFlagAdapter;
import com.massivecraft.factions.adapter.FPermAdapter;
import com.massivecraft.factions.adapter.FactionPreprocessAdapter;
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
import com.massivecraft.factions.chat.tag.ChatTagName;
import com.massivecraft.factions.chat.tag.ChatTagNameforce;
import com.massivecraft.factions.chat.tag.ChatTagRoleprefixforce;
import com.massivecraft.factions.chat.tag.ChatTagTitle;
import com.massivecraft.factions.cmd.*;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.factions.entity.MConfColl;
import com.massivecraft.factions.integration.dynmap.IntegrationDynmap;
import com.massivecraft.factions.integration.dynmap.IntegrationDynmapFactions;
import com.massivecraft.factions.integration.herochat.IntegrationHerochat;
import com.massivecraft.factions.integration.lwc.IntegrationLwc;
import com.massivecraft.factions.listeners.FactionsListenerChat;
import com.massivecraft.factions.listeners.FactionsListenerEcon;
import com.massivecraft.factions.listeners.FactionsListenerExploit;
import com.massivecraft.factions.listeners.FactionsListenerMain;
import com.massivecraft.factions.mixin.PowerMixin;
import com.massivecraft.factions.mixin.PowerMixinDefault;
import com.massivecraft.factions.task.TaskPlayerDataRemove;
import com.massivecraft.factions.task.TaskEconLandReward;
import com.massivecraft.factions.task.TaskPlayerPowerUpdate;
import com.massivecraft.massivecore.Aspect;
import com.massivecraft.massivecore.AspectColl;
import com.massivecraft.massivecore.MassivePlugin;
import com.massivecraft.massivecore.Multiverse;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.xlib.gson.Gson;
import com.massivecraft.massivecore.xlib.gson.GsonBuilder;


public class Factions extends MassivePlugin
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public final static String FACTION_MONEY_ACCOUNT_ID_PREFIX = "faction-"; 
	
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
	
	// Aspects
	private Aspect aspect;
	public Aspect getAspect() { return this.aspect; }
	public Multiverse getMultiverse() { return this.getAspect().getMultiverse(); }
	
	// Database Initialized
	private boolean databaseInitialized;
	public boolean isDatabaseInitialized() { return this.databaseInitialized; }
	
	// Mixins
	private PowerMixin powerMixin = null;
	public PowerMixin getPowerMixin() { return this.powerMixin == null ? PowerMixinDefault.get() : this.powerMixin; }
	public void setPowerMixin(PowerMixin powerMixin) { this.powerMixin = powerMixin; }
	
	// Gson without preprocessors
	public final Gson gsonWithoutPreprocessors = this.getGsonBuilderWithoutPreprocessors().create();

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void onEnable()
	{
		if ( ! preEnable()) return;
		
		// Initialize Aspects
		this.aspect = AspectColl.get().get(Const.ASPECT, true);
		this.aspect.register();
		this.aspect.setDesc(
			"<i>If the factions system even is enabled and how it's configured.",
			"<i>What factions exists and what players belong to them."
		);
		
		// Register Faction accountId Extractor
		// TODO: Perhaps this should be placed in the econ integration somewhere?
		MUtil.registerExtractor(String.class, "accountId", ExtractorFactionAccountId.get());

		// Initialize Database
		this.databaseInitialized = false;
		MConfColl.get().init();
		MPlayerColl.get().init();
		FactionColl.get().init();
		BoardColl.get().init();
		FactionColl.get().reindexMPlayers();
		this.databaseInitialized = true;
		
		// Commands
		this.outerCmdFactions = new CmdFactions();
		this.outerCmdFactions.register();

		// Setup Listeners
		FactionsListenerMain.get().setup();
		FactionsListenerChat.get().setup();
		FactionsListenerExploit.get().setup();
		
		// TODO: This listener is a work in progress.
		// The goal is that the Econ integration should be completely based on listening to our own events.
		// Right now only a few situations are handled through this listener.
		FactionsListenerEcon.get().setup();
		
		// Integrate
		this.integrate(
			IntegrationHerochat.get(),
			IntegrationLwc.get(),
			IntegrationDynmap.get(),
			IntegrationDynmapFactions.get()
		);
		
		// Schedule recurring non-tps-dependent tasks
		TaskPlayerPowerUpdate.get().activate(this);
		TaskPlayerDataRemove.get().activate(this);
		TaskEconLandReward.get().activate(this);
		
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
		ChatTagRoleprefixforce.get().register();
		ChatTagName.get().register();
		ChatTagNameforce.get().register();
		ChatTagTitle.get().register();
		
		postEnable();
	}
	
	public GsonBuilder getGsonBuilderWithoutPreprocessors()
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
	
	@Override
	public GsonBuilder getGsonBuilder()
	{
		return this.getGsonBuilderWithoutPreprocessors()
		.registerTypeAdapter(Faction.class, FactionPreprocessAdapter.get())
		;
	}
	
}
