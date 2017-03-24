package com.massivecraft.factions;

import com.massivecraft.factions.adapter.BoardAdapter;
import com.massivecraft.factions.adapter.BoardMapAdapter;
import com.massivecraft.factions.adapter.RelAdapter;
import com.massivecraft.factions.adapter.TerritoryAccessAdapter;
import com.massivecraft.factions.chat.modifier.ChatModifierLc;
import com.massivecraft.factions.chat.modifier.ChatModifierLp;
import com.massivecraft.factions.chat.modifier.ChatModifierParse;
import com.massivecraft.factions.chat.modifier.ChatModifierRp;
import com.massivecraft.factions.chat.modifier.ChatModifierUc;
import com.massivecraft.factions.chat.modifier.ChatModifierUcf;
import com.massivecraft.factions.chat.tag.ChatTagName;
import com.massivecraft.factions.chat.tag.ChatTagNameforce;
import com.massivecraft.factions.chat.tag.ChatTagRelcolor;
import com.massivecraft.factions.chat.tag.ChatTagRole;
import com.massivecraft.factions.chat.tag.ChatTagRoleprefix;
import com.massivecraft.factions.chat.tag.ChatTagRoleprefixforce;
import com.massivecraft.factions.chat.tag.ChatTagTitle;
import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.cmd.type.TypeFactionChunkChangeType;
import com.massivecraft.factions.cmd.type.TypeRel;
import com.massivecraft.factions.engine.EngineCanCombatHappen;
import com.massivecraft.factions.engine.EngineChat;
import com.massivecraft.factions.engine.EngineChunkChange;
import com.massivecraft.factions.engine.EngineDenyCommands;
import com.massivecraft.factions.engine.EngineEcon;
import com.massivecraft.factions.engine.EngineExploit;
import com.massivecraft.factions.engine.EngineFlagEndergrief;
import com.massivecraft.factions.engine.EngineFlagExplosion;
import com.massivecraft.factions.engine.EngineFlagFireSpread;
import com.massivecraft.factions.engine.EngineFlagSpawn;
import com.massivecraft.factions.engine.EngineFlagZombiegrief;
import com.massivecraft.factions.engine.EngineLastActivity;
import com.massivecraft.factions.engine.EngineMotd;
import com.massivecraft.factions.engine.EngineMoveChunk;
import com.massivecraft.factions.engine.EnginePermBuild;
import com.massivecraft.factions.engine.EnginePlayerData;
import com.massivecraft.factions.engine.EnginePower;
import com.massivecraft.factions.engine.EngineSeeChunk;
import com.massivecraft.factions.engine.EngineShow;
import com.massivecraft.factions.engine.EngineTeleportHomeOnDeath;
import com.massivecraft.factions.engine.EngineTerritoryShield;
import com.massivecraft.factions.engine.EngineVisualizations;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConfColl;
import com.massivecraft.factions.entity.MFlagColl;
import com.massivecraft.factions.entity.MPermColl;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.factions.entity.migrator.MigratorMConf001EnumerationUtil;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.factions.integration.V19.IntegrationV19;
import com.massivecraft.factions.integration.herochat.IntegrationHerochat;
import com.massivecraft.factions.integration.lwc.IntegrationLwc;
import com.massivecraft.factions.integration.spigot.IntegrationSpigot;
import com.massivecraft.factions.integration.worldguard.IntegrationWorldGuard;
import com.massivecraft.factions.mixin.PowerMixin;
import com.massivecraft.factions.task.TaskEconLandReward;
import com.massivecraft.factions.task.TaskFlagPermCreate;
import com.massivecraft.factions.task.TaskPlayerDataRemove;
import com.massivecraft.factions.task.TaskPlayerPowerUpdate;
import com.massivecraft.massivecore.MassivePlugin;
import com.massivecraft.massivecore.command.type.RegistryType;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.xlib.gson.GsonBuilder;
import org.bukkit.ChatColor;

public class Factions extends MassivePlugin
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public final static String FACTION_MONEY_ACCOUNT_ID_PREFIX = "faction-"; 
	
	public final static String ID_NONE = "none";
	public final static String ID_SAFEZONE = "safezone";
	public final static String ID_WARZONE = "warzone";
	
	public final static String NAME_NONE_DEFAULT = ChatColor.DARK_GREEN.toString() + "Wilderness";
	public final static String NAME_SAFEZONE_DEFAULT = "SafeZone";
	public final static String NAME_WARZONE_DEFAULT = "WarZone";
	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static Factions i;
	public static Factions get() { return i; }
	public Factions() { Factions.i = this; }
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	// Database Initialized
	private boolean databaseInitialized;
	public boolean isDatabaseInitialized() { return this.databaseInitialized; }
	
	// Mixins
	@Deprecated public PowerMixin getPowerMixin() { return PowerMixin.get(); }
	@Deprecated public void setPowerMixin(PowerMixin powerMixin) { PowerMixin.get().setInstance(powerMixin); }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void onEnableInner()
	{
		// Register types
		RegistryType.register(Rel.class, TypeRel.get());
		RegistryType.register(EventFactionsChunkChangeType.class, TypeFactionChunkChangeType.get());
		
		// Register Faction accountId Extractor
		// TODO: Perhaps this should be placed in the econ integration somewhere?
		MUtil.registerExtractor(String.class, "accountId", ExtractorFactionAccountId.get());

		// Initialize Database
		// MConf should always be activated first for all plugins. It's simply a standard. The config should have no dependencies.
		// MFlag and MPerm are both dependency free.
		// Next we activate Faction, MPlayer and Board. The order is carefully chosen based on foreign keys and indexing direction.
		// MPlayer --> Faction
		// We actually only have an index that we maintain for the MPlayer --> Faction one.
		// The Board could currently be activated in any order but the current placement is an educated guess.
		// In the future we might want to find all chunks from the faction or something similar.
		// We also have the /f access system where the player can be granted specific access, possibly supporting the idea of such a reverse index.
		this.databaseInitialized = false;
		MigratorMConf001EnumerationUtil.get().setActive(true);
		MConfColl.get().setActive(true);
		MFlagColl.get().setActive(true);
		MPermColl.get().setActive(true);
		FactionColl.get().setActive(true);
		MPlayerColl.get().setActive(true);
		BoardColl.get().setActive(true);
		this.databaseInitialized = true;
		
		// Activate
		this.activate(
			// Command
			CmdFactions.class,
		
			// Engines
			EngineCanCombatHappen.class,
			EngineChat.class,
			EngineChunkChange.class,
			EngineDenyCommands.class,
			EngineExploit.class,
			EngineFlagEndergrief.class,
			EngineFlagExplosion.class,
			EngineFlagFireSpread.class,
			EngineFlagSpawn.class,
			EngineFlagZombiegrief.class,
			EngineLastActivity.class,
			EngineMotd.class,
			EngineMoveChunk.class,
			EnginePermBuild.class,
			EnginePlayerData.class,
			EnginePower.class,
			EngineSeeChunk.class,
			EngineShow.class,
			EngineTeleportHomeOnDeath.class,
			EngineTerritoryShield.class,
			EngineVisualizations.class,
			EngineEcon.class, // TODO: Take an extra look and make sure all economy stuff is handled using events.

			// Integrate
			IntegrationHerochat.class,
			IntegrationLwc.class,
			IntegrationWorldGuard.class,
			IntegrationV19.class,
			
			// Spigot
			IntegrationSpigot.class,
			
			// Modulo Repeat Tasks
			TaskPlayerPowerUpdate.class,
			TaskPlayerDataRemove.class,
			TaskEconLandReward.class,
			TaskFlagPermCreate.class,
				
			// ChatModifiers
			ChatModifierLc.class,
			ChatModifierLp.class,
			ChatModifierParse.class,
			ChatModifierRp.class,
			ChatModifierUc.class,
			ChatModifierUcf.class,
				
			// ChatTags,
			ChatTagRelcolor.class,
			ChatTagRole.class,
			ChatTagRoleprefix.class,
			ChatTagRoleprefixforce.class,
			ChatTagName.class,
			ChatTagNameforce.class,
			ChatTagTitle.class
		);
	}
	
	@Override
	public GsonBuilder getGsonBuilder()
	{
		return super.getGsonBuilder()
		.registerTypeAdapter(TerritoryAccess.class, TerritoryAccessAdapter.get())
		.registerTypeAdapter(Board.class, BoardAdapter.get())
		.registerTypeAdapter(Board.MAP_TYPE, BoardMapAdapter.get())
		.registerTypeAdapter(Rel.class, RelAdapter.get())
		;
	}
	
}
