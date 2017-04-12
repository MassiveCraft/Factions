package com.massivecraft.factions.entity;

import com.massivecraft.factions.Factions;
import com.massivecraft.massivecore.store.SenderColl;
import org.bukkit.Bukkit;

import java.util.Collection;

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
	// EXTRAS
	// -------------------------------------------- //
	
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
