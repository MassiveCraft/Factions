package com.massivecraft.factions.entity;

import com.massivecraft.factions.Factions;
import com.massivecraft.massivecore.store.SenderColl;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.Txt;
import com.massivecraft.massivecore.xlib.gson.JsonObject;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.Map.Entry;

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
			String beforeId = mplayer.getFactionId();
			String afterId = null;
			mplayer.updateFactionIndexes(beforeId, afterId);
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
		String beforeId = null;
		if (mplayer == null) mplayer = this.id2entity.get(id);
		if (mplayer != null) beforeId = mplayer.getFactionId();
		
		// Super
		super.loadFromRemoteFixed(id, remoteEntry);
		
		// After
		String afterId = null;
		if (mplayer == null) mplayer = this.id2entity.get(id);
		if (mplayer != null) afterId = mplayer.getFactionId();
		
		// Perform
		if (mplayer != null) mplayer.updateFactionIndexes(beforeId, afterId);
	}
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public int clean()
	{
		int ret = 0;
		
		if (!FactionColl.get().isActive()) return ret;
		
		// For each player ...
		for (MPlayer mplayer : this.getAll())
		{
			// ... who doesn't have a valid faction ...
			String factionId = mplayer.getFactionId();
			if (FactionColl.get().containsId(factionId)) continue;
			
			// ... reset their faction data ...
			mplayer.resetFactionData();
			ret += 1;
			
			// ... and log.
			String message = Txt.parse("<i>Reset data for <h>%s <i>. Unknown factionId <h>%s", mplayer.getDisplayName(IdUtil.getConsole()), factionId);
			Factions.get().log(message);
		}
		
		return ret;
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
				// For each offline player ...
				for (MPlayer mplayer : mplayersOffline)
				{
					// ... see if they should be removed.
					mplayer.considerRemovePlayerMillis(true);
				}
			}
		});
	}
	
}
