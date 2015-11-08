package com.massivecraft.factions.update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.store.Coll;
import com.massivecraft.massivecore.store.Db;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.store.MStore;
import com.massivecraft.massivecore.util.MUtil;

public class UpdateUtil
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public static final List<String> oldCollnamePrefixes = MUtil.list(
		"factions_board@",
		"factions_faction@",
		"factions_uplayer@",
		"factions_uconf@"
	);
	
	// -------------------------------------------- //
	// UPDATE
	// -------------------------------------------- //
	// Assumes the MConfColl has been inited!
	
	public static void update()
	{
		// Select the old universe of our attention ...
		String universe = getUniverse();
		if (universe == null) return;
		
		Factions.get().log("Updating Database to New Version!");
		
		// ... load the old uconf data ...
		OldConfColls.get().init();
		OldConf oldConf = OldConfColls.get().getForUniverse(universe).get(MassiveCore.INSTANCE, true);
		
		// ... transfer the old uconf data over to the new mconf ...
		oldConf.transferTo(MConf.get());
		MConf.get().changed();
		MConf.get().sync();
		
		// ... rename target collections ...
		Db db = MStore.getDb();
		
		// The old mplayer data we don't care much for.
		// Could even delete it but let's just move it out of the way.
		db.renameColl(Const.COLLECTION_MPLAYER, "old_"+Const.COLLECTION_MPLAYER);
		
		db.renameColl("factions_board@" + universe, Const.COLLECTION_BOARD);
		db.renameColl("factions_faction@" + universe, Const.COLLECTION_FACTION);
		db.renameColl("factions_uplayer@" + universe, Const.COLLECTION_MPLAYER);
		
		// ... rename remaining collections ...
		for (String collname : db.getCollnames())
		{
			if (!collname.startsWith("factions_")) continue;
			if (!collname.contains("@")) continue;
			db.renameColl(collname, "old_" + collname);
		}
		
	}
	
	// -------------------------------------------- //
	// UNIVERSE SELECTION
	// -------------------------------------------- //
	
	public static String getUniverse()
	{
		List<String> universes = getUniverses();
		
		String ret = null;
		int best = -1;
		
		for (String universe : universes)
		{
			int count = getUniverseFactionCount(universe);
			if (count > 0 && count > best)
			{
				ret = universe;
				best = count;
			}
		}
		
		return ret;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int getUniverseFactionCount(String universe)
	{
		Coll coll = new Coll("factions_faction@"+universe, Entity.class, MStore.getDb(), Factions.get());
		
		Collection<String> ids = MStore.getDb().getIds(coll);
		
		return ids.size();
	}
	
	public static List<String> getUniverses()
	{
		List<String> ret = new ArrayList<String>();
		
		for (String collname : MStore.getDb().getCollnames())
		{
			for (String prefix : oldCollnamePrefixes)
			{
				if (collname.startsWith(prefix))
				{
					ret.add(collname.substring(prefix.length()));
				}
			}
		}
		
		return ret;
	}
	
	// -------------------------------------------- //
	// UPDATE SPECIAL IDS
	// -------------------------------------------- //
	
	public static void updateSpecialIds()
	{
		if (MConf.get().factionIdNone != null)
		{
			updateSpecialId(MConf.get().factionIdNone, Factions.ID_NONE);
			MConf.get().factionIdNone = null;
		}
		
		if (MConf.get().factionIdSafezone != null)
		{
			updateSpecialId(MConf.get().factionIdSafezone, Factions.ID_SAFEZONE);
			MConf.get().factionIdSafezone = null;
		}
		
		if (MConf.get().factionIdWarzone != null)
		{
			updateSpecialId(MConf.get().factionIdWarzone, Factions.ID_WARZONE);
			MConf.get().factionIdWarzone = null;
		}
		
		MConf.get().sync();
	}
	
	public static void updateSpecialId(String from, String to)
	{
		// Get the coll.
		FactionColl coll = FactionColl.get();
		
		// Get the faction and detach it
		Faction faction = coll.detachId(from);
		if (faction == null) return;
		coll.syncId(from);
		
		// A faction may already be occupying the to-id.
		// We must remove it to make space for renaming.
		// This faction is simply an auto-created faction with no references yet.
		coll.detachId(to);
		coll.syncId(to);
		
		// Attach it
		coll.attach(faction, to);
		coll.syncId(to);
		
		// Update that config special config option.
		if (MConf.get().defaultPlayerFactionId.equals(from))
		{
			MConf.get().defaultPlayerFactionId = to;
			MConf.get().sync();
		}
		
		// Update all board entries.
		updateBoards(from, to);
	}
	
	public static void updateBoards(String from, String to)
	{
		for (Board board : BoardColl.get().getAll())
		{
			updateBoard(board, from, to);
		}
	}
	
	public static void updateBoard(Board board, String from, String to)
	{
		boolean changed = false;
		for (TerritoryAccess ta : board.getMap().values())
		{
			changed |= updateTerritoryAccess(ta, from, to);
		}
		if (changed)
		{
			board.changed();
			board.sync();
		}
	}
	
	public static boolean updateTerritoryAccess(TerritoryAccess entity, String from, String to)
	{
		boolean changed = false;
		changed |= updateTerritoryHostFactionId(entity, from, to);
		changed |= updateTerritoryAccessFactionIds(entity, from, to);
		return changed;
	}

	public static boolean updateTerritoryHostFactionId(TerritoryAccess entity, String from, String to)
	{
		String before = entity.hostFactionId;
		if (before == null) return false;
		if (!before.equals(from)) return false;
		
		entity.hostFactionId = to;
		return true;
	}
	
	public static boolean updateTerritoryAccessFactionIds(TerritoryAccess entity, String from, String to)
	{
		// Before and After
		Set<String> before = entity.factionIds;
		if (before == null) return false;
		Set<String> after = new LinkedHashSet<String>();
		for (String id : before)
		{
			if (id == null) continue;
			if (id.equals(from))
			{
				after.add(to);
			}
			else
			{
				after.add(from);
			}
		}
		
		// NoChange
		if (MUtil.equals(before, after)) return false;
		
		// Apply
		entity.factionIds = after;
		//entity.sync();
		return true;
	}
	
}
