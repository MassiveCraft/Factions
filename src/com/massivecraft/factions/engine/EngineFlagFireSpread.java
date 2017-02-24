package com.massivecraft.factions.engine;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockSpreadEvent;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.ps.PS;

public class EngineFlagFireSpread extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static EngineFlagFireSpread i = new EngineFlagFireSpread();
	public static EngineFlagFireSpread get() { return i; }

	// -------------------------------------------- //
	// FLAG: FIRE SPREAD
	// -------------------------------------------- //

	public void blockFireSpread(Block block, Cancellable cancellable)
	{
		// If the faction at the block has firespread disabled ...
		PS ps = PS.valueOf(block);
		Faction faction = BoardColl.get().getFactionAt(ps);

		if (faction.getFlag(MFlag.getFlagFirespread())) return;

		// then cancel the event.
		cancellable.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockFireSpread(BlockIgniteEvent event)
	{
		// If fire is spreading ...
		if (event.getCause() != IgniteCause.SPREAD && event.getCause() != IgniteCause.LAVA) return;

		// ... consider blocking it.
		blockFireSpread(event.getBlock(), event);
	}

	// TODO: Is use of this event deprecated?
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockFireSpread(BlockSpreadEvent event)
	{
		// If fire is spreading ...
		if (event.getNewState().getType() != Material.FIRE) return;

		// ... consider blocking it.
		blockFireSpread(event.getBlock(), event);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockFireSpread(BlockBurnEvent event)
	{
		// If a block is burning ...

		// ... consider blocking it.
		blockFireSpread(event.getBlock(), event);
	}

}
