package com.massivecraft.factions.engine;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.mixin.MixinActual;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class EngineMotd extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineMotd i = new EngineMotd();
	public static EngineMotd get() { return i; }

	// -------------------------------------------- //
	// MOTD
	// -------------------------------------------- //

	public static void motd(PlayerJoinEvent event, EventPriority currentPriority)
	{
		// Gather info ...
		final Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;
		final MPlayer mplayer = MPlayer.get(player);
		final Faction faction = mplayer.getFaction();

		// ... if there is a motd ...
		if ( ! faction.hasMotd()) return;

		// ... and this is the priority we are supposed to act on ...
		if (currentPriority != MConf.get().motdPriority) return;

		// ... and this is an actual join ...
		if ( ! MixinActual.get().isActualJoin(event)) return;

		// ... then prepare the messages ...
		final List<Object> messages = faction.getMotdMessages();

		// ... and send to the player.
		if (MConf.get().motdDelayTicks < 0)
		{
			MixinMessage.get().messageOne(player, messages);
		}
		else
		{
			Bukkit.getScheduler().scheduleSyncDelayedTask(Factions.get(), new Runnable()
			{
				@Override
				public void run()
				{
					MixinMessage.get().messageOne(player, messages);
				}
			}, MConf.get().motdDelayTicks);
		}
	}

	// Can't be cancelled
	@EventHandler(priority = EventPriority.LOWEST)
	public void motdLowest(PlayerJoinEvent event)
	{
		motd(event, EventPriority.LOWEST);
	}

	// Can't be cancelled
	@EventHandler(priority = EventPriority.LOW)
	public void motdLow(PlayerJoinEvent event)
	{
		motd(event, EventPriority.LOW);
	}

	// Can't be cancelled
	@EventHandler(priority = EventPriority.NORMAL)
	public void motdNormal(PlayerJoinEvent event)
	{
		motd(event, EventPriority.NORMAL);
	}

	// Can't be cancelled
	@EventHandler(priority = EventPriority.HIGH)
	public void motdHigh(PlayerJoinEvent event)
	{
		motd(event, EventPriority.HIGH);
	}

	// Can't be cancelled
	@EventHandler(priority = EventPriority.HIGHEST)
	public void motdHighest(PlayerJoinEvent event)
	{
		motd(event, EventPriority.HIGHEST);
	}

	// Can't be cancelled
	@EventHandler(priority = EventPriority.MONITOR)
	public void motdMonitor(PlayerJoinEvent event)
	{
		motd(event, EventPriority.MONITOR);
	}

}
