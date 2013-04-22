package com.massivecraft.factions.entity;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.mcore.mixin.Mixin;
import com.massivecraft.mcore.store.MStore;
import com.massivecraft.mcore.store.SenderColl;
import com.massivecraft.mcore.util.TimeUnit;

public class FPlayerColl extends SenderColl<FPlayer>
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FPlayerColl(String name)
	{
		super(name, FPlayer.class, MStore.getDb(ConfServer.dburi), Factions.get());
	}
	
	// -------------------------------------------- //
	// OVERRIDE: COLL
	// -------------------------------------------- //

	@Override
	protected synchronized String attach(FPlayer entity, Object oid, boolean noteChange)
	{
		String ret = super.attach(entity, oid, noteChange);
		
		// If inited ...
		if (!this.inited()) return ret;
		if (!FactionColls.get().getForUniverse(this.getUniverse()).inited()) return ret;
		
		// ... update the index.
		Faction faction = entity.getFaction();
		faction.fplayers.add(entity);
		
		return ret;
	}
	
	@Override
	public FPlayer detachId(Object oid)
	{
		FPlayer ret = super.detachId(oid);
		if (ret == null) return null;
		
		// If inited ...
		if (!this.inited()) return ret;
		
		// ... update the index.
		Faction faction = ret.getFaction();
		faction.fplayers.remove(ret);
		
		return ret;
	}
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public void clean()
	{
		for (FPlayer fplayer : this.getAll())
		{
			if (FactionColls.get().get(this).containsId(fplayer.getFactionId())) continue;
			
			Factions.get().log("Reset faction data (invalid faction) for player "+fplayer.getName());
			fplayer.resetFactionData(false);
		}
	}
	
	public void autoLeaveOnInactivityRoutine()
	{
		if (ConfServer.autoLeaveAfterDaysOfInactivity <= 0.0) return;
		
		long now = System.currentTimeMillis();
		double toleranceMillis = ConfServer.autoLeaveAfterDaysOfInactivity * TimeUnit.MILLIS_PER_DAY;
		
		for (FPlayer fplayer : this.getAll())
		{
			Long lastPlayed = Mixin.getLastPlayed(fplayer.getId());
			if (lastPlayed == null) continue;
			
			if (fplayer.isOnline()) continue;
			if (now - lastPlayed <= toleranceMillis) continue;
			
			if (MConf.get().logFactionLeave || MConf.get().logFactionKick)
			{
				Factions.get().log("Player "+fplayer.getName()+" was auto-removed due to inactivity.");
			}

			// if player is faction leader, sort out the faction since he's going away
			if (fplayer.getRole() == Rel.LEADER)
			{
				Faction faction = fplayer.getFaction();
				if (faction != null)
				{
					fplayer.getFaction().promoteNewLeader();
				}
			}

			fplayer.leave(false);
			fplayer.detach();
		}
	}
}
