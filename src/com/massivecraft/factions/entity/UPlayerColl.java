package com.massivecraft.factions.entity;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.massivecore.mixin.Mixin;
import com.massivecraft.massivecore.store.MStore;
import com.massivecraft.massivecore.store.SenderColl;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;

public class UPlayerColl extends SenderColl<UPlayer>
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public UPlayerColl(String name)
	{
		super(name, UPlayer.class, MStore.getDb(), Factions.get());
	}
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public void clean()
	{
		FactionColl factionColl = FactionColls.get().get(this);
		String universe = this.getUniverse();
		for (UPlayer uplayer : this.getAll())
		{
			String factionId = uplayer.getFactionId();
			if (factionColl.containsId(factionId)) continue;
			
			uplayer.resetFactionData();
			
			String message = Txt.parse("<i>Reset data for <h>%s <i>in <h>%s <i>universe. Unknown factionId <h>%s", uplayer.getDisplayName(), universe, factionId);
			Factions.get().log(message);
		}
	}
	
	public void removePlayerDataAfterInactiveDaysRoutine()
	{
		if (MConf.get().removePlayerDataAfterInactiveDays <= 0.0) return;
		
		long now = System.currentTimeMillis();
		double toleranceMillis = MConf.get().removePlayerDataAfterInactiveDays * TimeUnit.MILLIS_PER_DAY;
		
		for (UPlayer uplayer : this.getAll())
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
}
