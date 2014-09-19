package com.massivecraft.factions.integration.lwc;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsChunkChange;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.massivecore.EngineAbstract;
import com.massivecraft.massivecore.ps.PS;


public class EngineLwc extends EngineAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineLwc i = new EngineLwc();
	public static EngineLwc get() { return i; }
	private EngineLwc() {}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Plugin getPlugin()
	{
		return Factions.get();
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
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public static void removeAlienProtections(PS chunkPs, Faction faction)
	{
		final List<MPlayer> nonAliens = faction.getMPlayers();
		
		final Chunk chunk = chunkPs.asBukkitChunk();
		final LWC lwc = LWC.getInstance();
		
		// run 
		Bukkit.getScheduler().runTask(Factions.get(), new Runnable()
		{
			@Override
			public void run()
			{
				for (int x = 0; x < 16; x++)
				{
					for (int z = 0; z < 16; z++)
					{
						for (int y = 0; y < chunk.getWorld().getMaxHeight(); y++)
						{
							Block block = chunk.getBlock(x, y, z);
							
							if (lwc.isProtectable(block))
							{
								Protection protection = lwc.findProtection(block);
								
								if (null != protection)
								{
									MPlayer player = MPlayer.get(protection.getOwner());
									
									if (!nonAliens.contains(player)) protection.remove();
								}
							}
						}
					}
				}				
			}
		});
	}
}
