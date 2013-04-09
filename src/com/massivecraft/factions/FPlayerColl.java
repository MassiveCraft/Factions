package com.massivecraft.factions;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.zcore.persist.PlayerEntityCollection;

public class FPlayerColl extends PlayerEntityCollection<FPlayer>
{
	public static FPlayerColl i = new FPlayerColl();
	
	Factions p = Factions.get();
	
	private FPlayerColl()
	{
		super
		(
			FPlayer.class,
			new CopyOnWriteArrayList<FPlayer>(),
			new ConcurrentSkipListMap<String, FPlayer>(String.CASE_INSENSITIVE_ORDER),
			new File(Factions.get().getDataFolder(), "players.json"),
			Factions.get().gson
		);
		
		this.setCreative(true);
	}
	
	@Override
	public Type getMapType()
	{
		return new TypeToken<Map<String, FPlayer>>(){}.getType();
	}
	
	public void clean()
	{
		for (FPlayer fplayer : this.get())
		{
			if ( ! FactionColl.i.exists(fplayer.getFactionId()))
			{
				p.log("Reset faction data (invalid faction) for player "+fplayer.getName());
				fplayer.resetFactionData(false);
			}
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
		
		for (FPlayer fplayer : FPlayerColl.i.get())
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
