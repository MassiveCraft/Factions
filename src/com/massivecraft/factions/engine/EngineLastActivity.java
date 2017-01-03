package com.massivecraft.factions.engine;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.event.EventMassiveCorePlayerLeave;
import com.massivecraft.massivecore.util.MUtil;

public class EngineLastActivity extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineLastActivity i = new EngineLastActivity();
	public static EngineLastActivity get() { return i; }

	// -------------------------------------------- //
	// UPDATE LAST ACTIVITY
	// -------------------------------------------- //

	public static void updateLastActivity(CommandSender sender)
	{
		if (sender == null) throw new RuntimeException("sender");
		if (MUtil.isntSender(sender)) return;

		MPlayer mplayer = MPlayer.get(sender);
		mplayer.setLastActivityMillis();
	}

	public static void updateLastActivitySoon(final CommandSender sender)
	{
		if (sender == null) throw new RuntimeException("sender");
		Bukkit.getScheduler().scheduleSyncDelayedTask(Factions.get(), new Runnable()
		{
			@Override
			public void run()
			{
				updateLastActivity(sender);
			}
		});
	}

	// Can't be cancelled
	@EventHandler(priority = EventPriority.LOWEST)
	public void updateLastActivity(PlayerJoinEvent event)
	{
		// During the join event itself we want to be able to reach the old data.
		// That is also the way the underlying fallback Mixin system does it and we do it that way for the sake of symmetry.
		// For that reason we wait till the next tick with updating the value.
		updateLastActivitySoon(event.getPlayer());
	}

	// Can't be cancelled
	@EventHandler(priority = EventPriority.LOWEST)
	public void updateLastActivity(EventMassiveCorePlayerLeave event)
	{
		// Here we do however update immediately.
		// The player data should be fully updated before leaving the server.
		updateLastActivity(event.getPlayer());
	}

}
