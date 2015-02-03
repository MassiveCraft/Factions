package com.massivecraft.factions.entity;

import java.util.Collection;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.massivecore.store.MStore;
import com.massivecraft.massivecore.store.SenderColl;
import com.massivecraft.massivecore.util.IdUtil;
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
