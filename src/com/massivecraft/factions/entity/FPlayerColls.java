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

public class FPlayerColls extends Colls<FPlayerColl, FPlayer>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FPlayerColls i = new FPlayerColls();
	public static FPlayerColls get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: COLLS
	// -------------------------------------------- //
	
	@Override
	public FPlayerColl createColl(String collName)
	{
		return new FPlayerColl(collName);
	}

	@Override
	public Aspect getAspect()
	{
		return Factions.get().getAspect();
	}
	
	@Override
	public String getBasename()
	{
		return Const.COLLECTION_BASENAME_PLAYER;
	}
	
	@Override
	public FPlayerColl get(Object o)
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
		File oldFile = new File(Factions.get().getDataFolder(), "players.json");
		File newFile = new File(Factions.get().getDataFolder(), "players.json.migrated");
		
		// Already migrated?
		if ( ! oldFile.exists()) return;
		
		// Read the file content through GSON. 
		Type type = new TypeToken<Map<String, FPlayer>>(){}.getType();
		Map<String, FPlayer> id2fplayer = Factions.get().gson.fromJson(DiscUtil.readCatch(oldFile), type);
		
		// Set the data
		for (Entry<String, FPlayer> entry : id2fplayer.entrySet())
		{
			String playerId = entry.getKey();
			FPlayer fplayer = entry.getValue();
			FPlayerColls.get().getForUniverse(MCore.DEFAULT).create(playerId).load(fplayer);
		}
		
		// Mark as migrated
		oldFile.renameTo(newFile);
	}
	
}
