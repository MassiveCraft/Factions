package com.massivecraft.factions.entity;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.mcore.MCore;
import com.massivecraft.mcore.store.Coll;
import com.massivecraft.mcore.store.Colls;
import com.massivecraft.mcore.store.Entity;
import com.massivecraft.mcore.usys.Aspect;
import com.massivecraft.mcore.util.DiscUtil;
import com.massivecraft.mcore.util.MUtil;
import com.massivecraft.mcore.util.SenderUtil;
import com.massivecraft.mcore.xlib.gson.reflect.TypeToken;

public class UPlayerColls extends Colls<UPlayerColl, UPlayer>
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
		return Const.COLLECTION_BASENAME_UPLAYER;
	}
	
	@Override
	public UPlayerColl get(Object o)
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
		
		if (SenderUtil.isNonplayer(o))
		{
			return this.getForWorld(Bukkit.getWorlds().get(0).getName());
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
		Type type = new TypeToken<Map<String, UPlayer>>(){}.getType();
		Map<String, UPlayer> id2uplayer = Factions.get().gson.fromJson(DiscUtil.readCatch(oldFile), type);
		
		// The Coll
		UPlayerColl coll = this.getForUniverse(MCore.DEFAULT);
		
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
	
}
