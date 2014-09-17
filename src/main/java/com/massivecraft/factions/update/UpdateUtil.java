package com.massivecraft.factions.update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.store.Coll;
import com.massivecraft.massivecore.store.Db;
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
		db.getDriver().renameColl(db, Const.COLLECTION_MPLAYER, "old_"+Const.COLLECTION_MPLAYER);
		
		db.getDriver().renameColl(db, "factions_board@" + universe, Const.COLLECTION_BOARD);
		db.getDriver().renameColl(db, "factions_faction@" + universe, Const.COLLECTION_FACTION);
		db.getDriver().renameColl(db, "factions_uplayer@" + universe, Const.COLLECTION_MPLAYER);
		
		// ... rename remaining collections ...
		for (String collname : db.getCollnames())
		{
			if (!collname.startsWith("factions_")) continue;
			if (!collname.contains("@")) continue;
			db.getDriver().renameColl(db, collname, "old_" + collname);
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
	
	public static int getUniverseFactionCount(String universe)
	{
		Coll<Object> coll = new Coll<Object>("factions_faction@"+universe, Object.class, MStore.getDb(), Factions.get());
		
		Collection<String> ids = MStore.getDb().getDriver().getIds(coll);
		
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
	
}
