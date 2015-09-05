package com.massivecraft.factions.integration.worldguard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsChunksChange;
import com.massivecraft.massivecore.EngineAbstract;
import com.massivecraft.massivecore.ps.PS;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class EngineWorldGuard extends EngineAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineWorldGuard i = new EngineWorldGuard();
	public static EngineWorldGuard get() { return i; }
	private EngineWorldGuard() {}
	
	// -------------------------------------------- //
	// REFERENCES
	// -------------------------------------------- //
	
	WorldGuardPlugin worldGuard;	
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Plugin getPlugin()
	{
		return Factions.get();
	}
	
	@Override
	public void activate()
	{
		worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
				
		super.activate();
	}
	
	@Override
	public void deactivate()
	{		
		worldGuard = null;
		
		super.deactivate();
	}
	
	// -------------------------------------------- //
	// LISTENER
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void checkForRegion(EventFactionsChunksChange event)
	{
		// Permanent Factions should not apply this rule 
		if (event.getNewFaction().getFlag(MFlag.ID_PERMANENT)) return;
		
		MPlayer player = event.getMSender();
		
		// Admin and ops don't bother checking 
		if (player.isUsingAdminMode() || event.getSender().isOp()) return; 
		
		for (PS chunkChecking : event.getChunks())
		{
			// Grab any regions in the chunk
			List<ProtectedRegion> regions = getProtectedRegionsFor(chunkChecking);
			
			// Ensure there are actually regions to go over 
			if (regions == null || regions.isEmpty()) continue;
			
			for (ProtectedRegion region : regions)
			{
				// Ensure it's not the global region, and check if they're a member 
				if (region.getId() == "__global__" || region.getMembers().contains(player.getUuid())) continue;
				
				// Check for a permission
				if (!event.getMSender().getPlayer().hasPermission("factions.allowregionclaim." + region.getId()))
				{
					// No permission, notify player and stop claiming
					player.msg("<b>You cannot claim the chunk at %s, %s as there is a region in the way.", chunkChecking.getChunkX(), chunkChecking.getChunkZ());
					
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public List<ProtectedRegion> getProtectedRegionsFor(PS ps)
	{
		// Find overlaps in the chunk
		int minChunkX = ps.getChunkX() << 4;
		int minChunkZ = ps.getChunkZ() << 4;
		int maxChunkX = minChunkX + 15;
		int maxChunkZ = minChunkZ + 15;
		
		int worldHeight = ps.asBukkitWorld().getMaxHeight();

		BlockVector minChunk = new BlockVector(minChunkX, 0, minChunkZ);
		BlockVector maxChunk = new BlockVector(maxChunkX, worldHeight, maxChunkZ);
		
		RegionManager regionManager = worldGuard.getRegionManager(ps.asBukkitWorld());
		
		String regionName = "factions_temp";
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionName, minChunk, maxChunk);
		
		Map<String, ProtectedRegion> regionMap = regionManager.getRegions(); 
		List<ProtectedRegion> regionList = new ArrayList<ProtectedRegion>(regionMap.values());
		
		// Let's find what we've overlapped
		List<ProtectedRegion> overlapRegions = region.getIntersectingRegions(regionList);
			
		return overlapRegions;
		
	}
}
