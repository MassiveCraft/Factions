package com.massivecraft.factions.entity;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.mcore.MCore;
import com.massivecraft.mcore.store.Coll;
import com.massivecraft.mcore.store.Colls;
import com.massivecraft.mcore.store.Entity;
import com.massivecraft.mcore.usys.Aspect;
import com.massivecraft.mcore.util.DiscUtil;
import com.massivecraft.mcore.util.MUtil;
import com.massivecraft.mcore.xlib.gson.reflect.TypeToken;

public class FactionColls extends Colls<FactionColl, Faction>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FactionColls i = new FactionColls();
	public static FactionColls get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: COLLS
	// -------------------------------------------- //
	
	@Override
	public FactionColl createColl(String collName)
	{
		return new FactionColl(collName);
	}

	@Override
	public Aspect getAspect()
	{
		return Factions.get().getAspect();
	}
	
	@Override
	public String getBasename()
	{
		return Const.COLLECTION_BASENAME_FACTION;
	}
	
	@Override
	public FactionColl get(Object o)
	{
		if (o == null) return null;
		
		if (o instanceof Entity)
		{
			return this.getForUniverse(((Entity<?>)o).getUniverse());
		}
		
		if (o instanceof Coll)
		{
			return this.getForUniverse(((Coll<?>)o).getUniverse());
		}
		
		String worldName = MUtil.extract(String.class, "worldName", o);
		if (worldName == null) return null;
		return this.getForWorld(worldName);
	}
	
	@Override
	public void init()
	{
		super.init();

		this.migrate();
	}
	
	public void migrate()
	{
		// Create file objects
		File oldFile = new File(Factions.get().getDataFolder(), "factions.json");
		File newFile = new File(Factions.get().getDataFolder(), "factions.json.migrated");
		
		// Already migrated?
		if ( ! oldFile.exists()) return;
		
		// Read the file content through GSON. 
		Type type = new TypeToken<Map<String, Faction>>(){}.getType();
		Map<String, Faction> id2faction = Factions.get().gson.fromJson(DiscUtil.readCatch(oldFile), type);
		
		// Set the data
		for (Entry<String, Faction> entry : id2faction.entrySet())
		{
			String factionId = entry.getKey();
			Faction faction = entry.getValue();
			
			this.getForUniverse(MCore.DEFAULT).create(factionId).load(faction);
		}
		
		// Mark as migrated
		oldFile.renameTo(newFile);
	}
	
}
