package com.massivecraft.factions.engine;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerKickEvent;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.massivecore.Engine;

public class EnginePlayerData extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EnginePlayerData i = new EnginePlayerData();
	public static EnginePlayerData get() { return i; }

	// -------------------------------------------- //
	// REMOVE PLAYER DATA WHEN BANNED
	// -------------------------------------------- //

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event)
	{
		// If a player was kicked from the server ...
		Player player = event.getPlayer();

		// ... and if the if player was banned (not just kicked) ...
		//if (!event.getReason().equals("Banned by admin.")) return;
		if (!player.isBanned()) return;

		// ... and we remove player data when banned ...
		if (!MConf.get().removePlayerWhenBanned) return;

		// ... get rid of their stored info.
		MPlayer mplayer = MPlayerColl.get().get(player, false);
		if (mplayer == null) return;

		if (mplayer.getRole() == Rel.LEADER)
		{
			mplayer.getFaction().promoteNewLeader();
		}

		mplayer.leave();
		mplayer.detach();
	}

}
