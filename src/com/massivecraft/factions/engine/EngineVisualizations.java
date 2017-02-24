package com.massivecraft.factions.engine;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.util.MUtil;

public class EngineVisualizations extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineVisualizations i = new EngineVisualizations();
	public static EngineVisualizations get() { return i; }

	// -------------------------------------------- //
	// VISUALIZE UTIL
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMoveClearVisualizations(PlayerMoveEvent event)
	{
		if (MUtil.isSameBlock(event)) return;
		
		VisualizeUtil.clear(event.getPlayer());
	}

}
