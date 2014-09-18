package com.massivecraft.factions.integration.lwc;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.griefcraft.scripting.Module;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;
import com.griefcraft.scripting.event.LWCBlockInteractEvent;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsChunkChange;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.factions.listeners.FactionsListenerMain;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;


public class EngineLwc implements Listener
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineLwc i = new EngineLwc();
	public static EngineLwc get() { return i; }
	private EngineLwc() {}
	
	// -------------------------------------------- //
	// ACTIVATE & DEACTIVATE
	// -------------------------------------------- //
	
	public void activate()
	{
		Bukkit.getPluginManager().registerEvents(this, Factions.get());
	}
	
	public void deactivate()
	{
		HandlerList.unregisterAll(this);
	}
	
	// -------------------------------------------- //
	// LISTENER
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void removeProtectionsOnChunkChange(EventFactionsChunkChange event)
	{
		// If we are supposed to clear at this chunk change type ...
		Faction newFaction = event.getNewFaction();
		EventFactionsChunkChangeType type = event.getType();
		Boolean remove = MConf.get().lwcRemoveOnChange.get(type);
		if (remove == null) return;
		if (remove == false) return;
		
		// ... then remove for all other factions than the new one.
		removeAlienProtections(event.getChunk(), newFaction);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onCreateProtection(LWCProtectionRegisterEvent event)
	{
		if (FactionsListenerMain.canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), false)) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onInteractWithProtection(LWCBlockInteractEvent event)
	{		
		// Find existing protected block
		Protection protection = event.getLWC().findProtection(event.getBlock());
		if (protection == null) return;
		
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(event.getBlock()));
		MPlayer mplayer = MPlayer.get(event.getPlayer());
		
		// LWC Leader Access option
		if (MConf.get().lwcLeaderAccess && !event.getResult().equals(Module.Result.ALLOW) && faction.getLeader().equals(mplayer))
		{
			event.setResult(Module.Result.ALLOW);
		}
		
		// Check if the owner can build here
		if (FactionsListenerMain.canPlayerBuildAt(protection.getBukkitOwner(), PS.valueOf(event.getBlock()), false)) return;
		
		// They can't, so remove the existing protection 
		protection.remove();

	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	

	public static void removeAlienProtections(PS chunkPs, Faction faction)
	{
		final Chunk chunk = chunkPs.asBukkitChunk(true);
		final List<MPlayer> nonAliens = faction.getMPlayers();
		final List<Material> protectable = MUtil.list(
			Material.CHEST,
			Material.FURNACE,
			Material.BURNING_FURNACE,
			Material.WALL_SIGN,
			Material.SIGN_POST,
			Material.DISPENSER,
			Material.HOPPER,
			Material.WOODEN_DOOR,
			Material.IRON_DOOR_BLOCK,
			Material.TRAP_DOOR
		);
		
		Bukkit.getScheduler().runTask(Factions.get(), new Runnable()
		{
			@Override
			public void run() {
				for (int x = 0; x < 16; x++)
				{
					for (int z = 0; z < 16; z++)
					{
						for (int y = 0; y < 256; y++)
						{
							Block block = chunk.getBlock(x, y, z);
							
							Material blockType = block.getType();
							
							if (blockType == Material.AIR) continue; 
							
							if (protectable.contains(blockType))
							{
								Protection protection = LWC.getInstance().findProtection(block);
								if (protection == null) continue;
								
								MPlayer owner = MPlayer.get(protection.getOwner());
								
								if (!nonAliens.contains(owner)) protection.remove();
								
							}
						}
					}
				}
			}
		});
	}
}
