package com.massivecraft.factions;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import com.massivecraft.mcore.store.MStore;
import com.massivecraft.mcore.store.SenderColl;
import com.massivecraft.mcore.util.DiscUtil;
import com.massivecraft.mcore.xlib.gson.reflect.TypeToken;

public class FPlayerColl extends SenderColl<FPlayer>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FPlayerColl i = new FPlayerColl();
	public static FPlayerColl get() { return i; }
	private FPlayerColl()
	{
		super(Const.COLLECTION_BASENAME_PLAYER, FPlayer.class, MStore.getDb(ConfServer.dburi), Factions.get());
	}
	
	// -------------------------------------------- //
	// OVERRIDE: COLL
	// -------------------------------------------- //
	
	// TODO: Init and migration routine!
	
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
			FPlayerColl.get().create(playerId).load(fplayer);
		}
		
		// Mark as migrated
		oldFile.renameTo(newFile);
	}
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public void clean()
	{
		for (FPlayer fplayer : this.getAll())
		{
			if (FactionColl.get().containsId(fplayer.getFactionId())) continue;
			
			Factions.get().log("Reset faction data (invalid faction) for player "+fplayer.getName());
			fplayer.resetFactionData(false);
		}
	}
	
	public void autoLeaveOnInactivityRoutine()
	{
		if (ConfServer.autoLeaveAfterDaysOfInactivity <= 0.0)
		{
			return;
		}

		long now = System.currentTimeMillis();
		double toleranceMillis = ConfServer.autoLeaveAfterDaysOfInactivity * 24 * 60 * 60 * 1000;
		
		for (FPlayer fplayer : this.getAll())
		{
			if (fplayer.isOffline() && now - fplayer.getLastLoginTime() > toleranceMillis)
			{
				if (ConfServer.logFactionLeave || ConfServer.logFactionKick)
					Factions.get().log("Player "+fplayer.getName()+" was auto-removed due to inactivity.");

				// if player is faction leader, sort out the faction since he's going away
				if (fplayer.getRole() == Rel.LEADER)
				{
					Faction faction = fplayer.getFaction();
					if (faction != null)
						fplayer.getFaction().promoteNewLeader();
				}

				fplayer.leave(false);
				fplayer.detach();
			}
		}
	}
}
