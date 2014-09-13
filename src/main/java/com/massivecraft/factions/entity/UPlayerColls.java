package com.massivecraft.factions.entity;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.massivecore.Aspect;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.util.DiscUtil;
import com.massivecraft.massivecore.xlib.gson.reflect.TypeToken;

public class UPlayerColls extends XColls<UPlayerColl, UPlayer>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static UPlayerColls i = new UPlayerColls();
	public static UPlayerColls get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: COLLS
	// -------------------------------------------- //
	
	@Override
	public UPlayerColl createColl(String collName)
	{
		return new UPlayerColl(collName);
	}

	@Override
	public Aspect getAspect()
	{
		return Factions.get().getAspect();
	}
	
	@Override
	public String getBasename()
	{
		return Const.COLLECTION_UPLAYER;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		this.migrate();
	}
	
	// This method is for the 1.8.X --> 2.0.0 migration
	public void migrate()
	{
		// Create file objects
		File oldFile = new File(Factions.get().getDataFolder(), "players.json");
		File newFile = new File(Factions.get().getDataFolder(), "players.json.migrated");
		
		// Already migrated?
		if ( ! oldFile.exists()) return;
		
		// Read the file content through GSON.
		Type type = new TypeToken<Map<String, UPlayer>>(){}.getType();
		Map<String, UPlayer> id2uplayer = Factions.get().gson.fromJson(DiscUtil.readCatch(oldFile), type);
		
		// The Coll
		UPlayerColl coll = this.getForUniverse(MassiveCore.DEFAULT);
		
		// Set the data
		for (Entry<String, UPlayer> entry : id2uplayer.entrySet())
		{
			String playerId = entry.getKey();
			UPlayer uplayer = entry.getValue();
			coll.attach(uplayer, playerId);
		}
		
		// Mark as migrated
		oldFile.renameTo(newFile);
	}
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public void clean()
	{
		for (UPlayerColl coll : this.getColls())
		{
			coll.clean();
		}
	}
	
}
