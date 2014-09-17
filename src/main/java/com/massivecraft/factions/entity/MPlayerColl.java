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
		String universe = this.getUniverse();
		for (MPlayer uplayer : this.getAll())
		{
			String factionId = uplayer.getFactionId();
			if (FactionColl.get().containsId(factionId)) continue;
			
			uplayer.resetFactionData();
			
			String message = Txt.parse("<i>Reset data for <h>%s <i>in <h>%s <i>universe. Unknown factionId <h>%s", uplayer.getDisplayName(IdUtil.getConsole()), universe, factionId);
			Factions.get().log(message);
		}
	}
	
	public void removePlayerDataAfterInactiveDaysRoutine()
	{
		if (MConf.get().removePlayerDataAfterInactiveDays <= 0.0) return;
		
		long now = System.currentTimeMillis();
		double toleranceMillis = MConf.get().removePlayerDataAfterInactiveDays * TimeUnit.MILLIS_PER_DAY;
		
		for (MPlayer uplayer : this.getAll())
		{
			Long lastPlayed = Mixin.getLastPlayed(uplayer.getId());
			if (lastPlayed == null) continue;
			
			if (uplayer.isOnline()) continue;
			if (now - lastPlayed <= toleranceMillis) continue;
			
			if (MConf.get().logFactionLeave || MConf.get().logFactionKick)
			{
				Factions.get().log("Player "+uplayer.getName()+" was auto-removed due to inactivity.");
			}

			// if player is faction leader, sort out the faction since he's going away
			if (uplayer.getRole() == Rel.LEADER)
			{
				Faction faction = uplayer.getFaction();
				if (faction != null)
				{
					uplayer.getFaction().promoteNewLeader();
				}
			}

			uplayer.leave();
			uplayer.detach();
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
	 */
	
}
