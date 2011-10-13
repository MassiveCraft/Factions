package com.massivecraft.factions;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.zcore.persist.PlayerEntityCollection;

public class FPlayers extends PlayerEntityCollection<FPlayer>
{
	public static FPlayers i = new FPlayers();
	
	P p = P.p;
	
	private FPlayers()
	{
		super
		(
			FPlayer.class,
			new CopyOnWriteArrayList<FPlayer>(),
			new ConcurrentSkipListMap<String, FPlayer>(String.CASE_INSENSITIVE_ORDER),
			new File(P.p.getDataFolder(), "players.json"),
			P.p.gson
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
			if ( ! Factions.i.exists(fplayer.getFactionId()))
			{
				p.log("Reset faction data (invalid faction) for player "+fplayer.getName());
				fplayer.resetFactionData();
			}
		}
	}
	
	public void autoLeaveOnInactivityRoutine()
	{
		if (Conf.autoLeaveAfterDaysOfInactivity <= 0.0)
		{
			return;
		}

		long now = System.currentTimeMillis();
		double toleranceMillis = Conf.autoLeaveAfterDaysOfInactivity * 24 * 60 * 60 * 1000;
		
		for (FPlayer fplayer : FPlayers.i.get())
		{
			if (now - fplayer.getLastLoginTime() > toleranceMillis)
			{
				fplayer.leave(false);
				fplayer.markForDeletion(true);
			}
		}
	}
	
	
	// TODO: Intressant.... denna skulle jag kanske behöva undersöka lite mer... lägga till i core?
	// En form av match player name...
	public FPlayer find(String playername)
	{
		for (FPlayer fplayer : this.get())
		{
			if (fplayer.getId().equalsIgnoreCase(playername) || fplayer.getId().toLowerCase().startsWith(playername.toLowerCase()))
			{
				return fplayer;
			}
		}
		return null;
	}
	
	/*public Set<VPlayer> findAllOnlineInfected()
	{
		Set<VPlayer> vplayers = new HashSet<VPlayer>();
		for (VPlayer vplayer : this.getOnline())
		{
			if (vplayer.isInfected())
			{
				vplayers.add(vplayer);
			}
		}
		return vplayers;
	}
	
	public Set<VPlayer> findAllOnlineVampires()
	{
		Set<VPlayer> vplayers = new HashSet<VPlayer>();
		for (VPlayer vplayer : this.getOnline())
		{
			if (vplayer.isVampire())
			{
				vplayers.add(vplayer);
			}
		}
		return vplayers;
	}*/
}
