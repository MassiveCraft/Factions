package com.massivecraft.factions.entity;

import java.util.Collection;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Factions;
import com.massivecraft.massivecore.store.SenderColl;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.Txt;
import com.massivecraft.massivecore.xlib.gson.JsonObject;

public class MPlayerColl extends SenderColl<MPlayer>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static MPlayerColl i = new MPlayerColl();
	public static MPlayerColl get() { return i; }

	// -------------------------------------------- //
	// STACK TRACEABILITY
	// -------------------------------------------- //
	
	@Override
	public void onTick()
	{
		super.onTick();
	}
	
	// -------------------------------------------- //
	// UPDATE FACTION INDEXES
	// -------------------------------------------- //
	
	@Override
	public synchronized MPlayer removeAtLocalFixed(String id)
	{
		if (!Factions.get().isDatabaseInitialized()) return super.removeAtLocalFixed(id);
		
		MPlayer mplayer = this.id2entity.get(id);
		
		if (mplayer != null)
		{
			Faction before = mplayer.getFaction();
			Faction after = null;
			mplayer.updateFactionIndexes(before, after);
		}
		
		return super.removeAtLocalFixed(id);
	}
	
	@Override
	public synchronized void loadFromRemoteFixed(String id, Entry<JsonObject, Long> remoteEntry)
	{
		if (!Factions.get().isDatabaseInitialized())
		{
			super.loadFromRemoteFixed(id, remoteEntry);
			return;
		}
		
		MPlayer mplayer = null;
		
		// Before
		Faction before = null;
		if (mplayer == null) mplayer = this.id2entity.get(id);
		if (mplayer != null) before = mplayer.getFaction();
		
		// Super
		super.loadFromRemoteFixed(id, remoteEntry);
		
		// After
		Faction after = null;
		if (mplayer == null) mplayer = this.id2entity.get(id);
		if (mplayer != null) after = mplayer.getFaction();
		
		// Perform
		if (mplayer != null) mplayer.updateFactionIndexes(before, after);
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
	
	public void considerRemovePlayerMillis()
	{
		// If the config option is 0 or below that means the server owner want it disabled.
		if (MConf.get().removePlayerMillisDefault <= 0.0) return;
		
		// For each of the offline players...
		// NOTE: If the player is currently online it's most definitely not inactive.
		// NOTE: This check catches some important special cases like the @console "player".
		final Collection<MPlayer> mplayersOffline = this.getAllOffline();
		
		Bukkit.getScheduler().runTaskAsynchronously(Factions.get(), new Runnable()
		{
			@Override
			public void run()
			{
				for (MPlayer mplayer : mplayersOffline)
				{
					mplayer.considerRemovePlayerMillis(true);
				}
			}
		});
	}
	
}
