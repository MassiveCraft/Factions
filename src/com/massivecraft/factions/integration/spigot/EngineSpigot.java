package com.massivecraft.factions.integration.spigot;

import com.massivecraft.factions.engine.EnginePermBuild;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.List;


public class EngineSpigot extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineSpigot i = new EngineSpigot();
	public static EngineSpigot get() { return i; }
	
	// -------------------------------------------- //
	// LISTENER
	// -------------------------------------------- //
	
	// This is a special Spigot event that fires for Minecraft 1.8 armor stands.
	// It also fires for other entity types but for those the event is buggy.
	// It seems we can only cancel interaction with armor stands from here.
	// Thus we only handle armor stands from here and handle everything else in EngineMain.
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event)
	{
		// Ignore Off Hand
		if (isOffHand(event)) return;
		
		// Gather Info
		final Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;
		final Entity entity = event.getRightClicked();
		final boolean verboose = true;
		
		// Only care for armor stands.
		if (entity.getType() != EntityType.ARMOR_STAND) return;
		
		// If we can't use ...
		if (EnginePermBuild.canPlayerUseEntity(player, entity, verboose)) return;
		
		// ... block use.
		event.setCancelled(true);
	}
	
	/*
	 * Note: With 1.8 and the slime blocks, retracting and extending pistons 
	 * became more of a problem. Blocks located on the border of a chunk
	 * could have easily been stolen. That is the reason why every block
	 * needs to be checked now, whether he moved into a territory which 
	 * he actually may not move into.
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockPistonExtendEvent event)
	{
		// Is checking deactivated by MConf?
		if ( ! MConf.get().handlePistonProtectionThroughDenyBuild) return;
		
		Faction pistonFaction = BoardColl.get().getFactionAt(PS.valueOf(event.getBlock()));
		
		List<Block> blocks = event.getBlocks();
		
		// Check for all extended blocks
		for (Block block : blocks)
		{
			// Block which is being pushed into
			Block targetBlock = block.getRelative(event.getDirection());
			
			// Members of a faction might not have build rights in their own territory, but pistons should still work regardless
			Faction targetFaction = BoardColl.get().getFactionAt(PS.valueOf(targetBlock));
			if (targetFaction == pistonFaction) continue;
			
			// Perm check
			if (MPerm.getPermBuild().has(pistonFaction, targetFaction)) continue;
			
			event.setCancelled(true);
			return;
		}	
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockPistonRetractEvent event)
	{	
		// Is checking deactivated by MConf?
		if ( ! MConf.get().handlePistonProtectionThroughDenyBuild) return;
		
		Faction pistonFaction = BoardColl.get().getFactionAt(PS.valueOf(event.getBlock()));
		
		List<Block> blocks = event.getBlocks();
		
		// Check for all retracted blocks
		for (Block block : blocks)
		{
			// Is the retracted block air/water/lava? Don't worry about it
			if (block.isEmpty() || block.isLiquid()) return;
			
			// Members of a faction might not have build rights in their own territory, but pistons should still work regardless
			Faction targetFaction = BoardColl.get().getFactionAt(PS.valueOf(block));
			if (targetFaction == pistonFaction) continue;

			// Perm check
			if (MPerm.getPermBuild().has(pistonFaction, targetFaction)) continue;
			
			event.setCancelled(true);
			return;
		}
	}
	
}
