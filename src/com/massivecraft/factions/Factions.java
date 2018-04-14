package com.massivecraft.factions;

import com.massivecraft.factions.adapter.BoardAdapter;
import com.massivecraft.factions.adapter.BoardMapAdapter;
import com.massivecraft.factions.adapter.RelAdapter;
import com.massivecraft.factions.adapter.TerritoryAccessAdapter;
import com.massivecraft.factions.chat.ChatActive;
import com.massivecraft.factions.cmd.type.TypeFactionChunkChangeType;
import com.massivecraft.factions.cmd.type.TypeRel;
import com.massivecraft.factions.engine.EngineEcon;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConfColl;
import com.massivecraft.factions.entity.MFlagColl;
import com.massivecraft.factions.entity.MPermColl;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.factions.mixin.PowerMixin;
import com.massivecraft.massivecore.MassivePlugin;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.command.type.RegistryType;
import com.massivecraft.massivecore.store.migrator.MigratorUtil;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.xlib.gson.GsonBuilder;
import org.bukkit.ChatColor;

import java.util.List;

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

		MigratorUtil.addJsonRepresentation(Board.class, Board.MAP_TYPE);

		// Activate
		this.activateAuto();
		this.activate(this.getClassesActive("chat", ChatActive.class));

	}

	@Override
	public List<Class<?>> getClassesActiveColls()
	{
		// MConf should always be activated first for all plugins. It's simply a standard. The config should have no dependencies.
		// MFlag and MPerm are both dependency free.
		// Next we activate Faction, MPlayer and Board. The order is carefully chosen based on foreign keys and indexing direction.
		// MPlayer --> Faction
		// We actually only have an index that we maintain for the MPlayer --> Faction one.
		// The Board could currently be activated in any order but the current placement is an educated guess.
		// In the future we might want to find all chunks from the faction or something similar.
		// We also have the /f access system where the player can be granted specific access, possibly supporting the idea of such a reverse index.
		return new MassiveList<Class<?>>(
			MConfColl.class,
			MFlagColl.class,
			MPermColl.class,
			FactionColl.class,
			MPlayerColl.class,
			BoardColl.class
		);
	}

	@Override
	public List<Class<?>> getClassesActiveEngines()
	{
		List<Class<?>> ret = super.getClassesActiveEngines();

		ret.remove(EngineEcon.class);
		ret.add(EngineEcon.class);

		return ret;
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
