package com.massivecraft.factions;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.struct.Role;
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
				fplayer.resetFactionData(false);
			}
		}
	}
}
