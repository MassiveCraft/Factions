package com.massivecraft.factions.entity;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.massivecore.mixin.Mixin;
import com.massivecraft.massivecore.store.MStore;
import com.massivecraft.massivecore.store.SenderColl;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;

public class MPlayerColl extends SenderColl<MPlayer>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static MPlayerColl i = new MPlayerColl();
	public static MPlayerColl get() { return i; }
	private MPlayerColl()
	{
		super(Const.COLLECTION_MPLAYER, MPlayer.class, MStore.getDb(), Factions.get());
	}
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public void clean()
	{
		for (MPlayer mplayer : this.getAll())
		{
			String factionId = mplayer.getFactionId();
			if (FactionColl.get().containsId(factionId)) continue;
			
			mplayer.resetFactionData();
			
			String message = Txt.parse("<i>Reset data for <h>%s <i>. Unknown factionId <h>%s", mplayer.getDisplayName(IdUtil.getConsole()), factionId);
			Factions.get().log(message);
		}
	}
	
	public void removePlayerDataAfterInactiveDaysRoutine()
	{
		if (MConf.get().removePlayerDataAfterInactiveDays <= 0.0) return;
		
		long now = System.currentTimeMillis();
		double toleranceMillis = MConf.get().removePlayerDataAfterInactiveDays * TimeUnit.MILLIS_PER_DAY;
		
		for (MPlayer mplayer : this.getAll())
		{
			// This may or may not be required.
			// Some users have been reporting a loop issue with the same player detaching over and over again.
			// Maybe skipping ahead if the player is detached will solve the issue.
			if (mplayer.detached()) continue;
			
			Long lastPlayed = Mixin.getLastPlayed(mplayer.getId());
			if (lastPlayed == null) continue;
			
			if (mplayer.isOnline()) continue;
			if (now - lastPlayed <= toleranceMillis) continue;
			
			if (MConf.get().logFactionLeave || MConf.get().logFactionKick)
			{
				Factions.get().log("Player "+mplayer.getName()+" was auto-removed due to inactivity.");
			}

			// if player is faction leader, sort out the faction since he's going away
			if (mplayer.getRole() == Rel.LEADER)
			{
				Faction faction = mplayer.getFaction();
				if (faction != null)
				{
					mplayer.getFaction().promoteNewLeader();
				}
			}

			mplayer.leave();
			mplayer.detach();
		}
	}
	
	/*
// This method is for the 1.8.X --> 2.0.0 migration
	public void migrate()
	{
		// Create file objects
		File oldFile = new File(Factions.get().getDataFolder(), "players.json");
		File newFile = new File(Factions.get().getDataFolder(), "players.json.migrated");
		
		// Already migrated?
		if ( ! oldFile.exists()) return;
		
		// Read the file content through GSON.
		Type type = new TypeToken<Map<String, MPlayer>>(){}.getType();
		Map<String, MPlayer> id2mplayer = Factions.get().gson.fromJson(DiscUtil.readCatch(oldFile), type);
		
		// The Coll
		MPlayerColl coll = this.getForUniverse(MassiveCore.DEFAULT);
		
		// Set the data
		for (Entry<String, MPlayer> entry : id2mplayer.entrySet())
		{
			String playerId = entry.getKey();
			MPlayer mplayer = entry.getValue();
			coll.attach(mplayer, playerId);
		}
		
		// Mark as migrated
		oldFile.renameTo(newFile);
	}
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public void clean()
	{
		for (MPlayerColl coll : this.getColls())
		{
			coll.clean();
		}
	}
	 */
	
}
