package com.massivecraft.factions.engine;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.EngineAbstract;
import com.massivecraft.massivecore.event.EventMassiveCorePlayerLeave;

public class EngineSeeChunk extends EngineAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineSeeChunk i = new EngineSeeChunk();
	public static EngineSeeChunk get() { return i; }
	public EngineSeeChunk() {}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Plugin getPlugin()
	{
		return Factions.get();
	}
	
	// -------------------------------------------- //
	// LEAVE AND WORLD CHANGE REMOVAL
	// -------------------------------------------- //

	public static void leaveAndWorldChangeRemoval(Player player)
	{
		final MPlayer mplayer = MPlayer.get(player);
		mplayer.setSeeingChunk(false);
	}
	
	// Can't be cancelled
	@EventHandler(priority = EventPriority.NORMAL)
	public void leaveAndWorldChangeRemoval(EventMassiveCorePlayerLeave event)
	{
		leaveAndWorldChangeRemoval(event.getPlayer());
	}
	
	// Can't be cancelled
	@EventHandler(priority = EventPriority.NORMAL)
	public void leaveAndWorldChangeRemoval(PlayerChangedWorldEvent event)
	{
		leaveAndWorldChangeRemoval(event.getPlayer());
	}
	
}
