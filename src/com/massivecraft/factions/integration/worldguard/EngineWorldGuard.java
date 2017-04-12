package com.massivecraft.factions.integration.worldguard;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsChunksChange;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.ps.PS;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EngineWorldGuard extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineWorldGuard i = new EngineWorldGuard();
	public static EngineWorldGuard get() { return i; }
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	protected WorldGuardPlugin worldGuard;	
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void setActiveInner(boolean active)
	{
		if (active)
		{
			this.worldGuard = WGBukkit.getPlugin();
		}
		else
		{
			this.worldGuard = null;
		}
	}
	
	// -------------------------------------------- //
	// LISTENER
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void checkForRegion(EventFactionsChunksChange event)
	{
		// Skip checks if the configuration has worldguardCheckEnabled disabled
		if ( ! MConf.get().worldguardCheckEnabled) return; 
		
		// Permanent Factions should not apply this rule 
		if (event.getNewFaction().getFlag(MFlag.ID_PERMANENT)) return;
		
		MPlayer mplayer = event.getMPlayer();
		Player player = mplayer.getPlayer();
		
		// Only do this for players 
		if (player == null) return;
		
		LocalPlayer wrapperPlayer = this.worldGuard.wrapPlayer(player);
		
		if ( ! MConf.get().worldguardCheckWorldsEnabled.contains(player)) return;

		// For overriders don't bother checking 
		if (mplayer.isOverriding()) return; 
		
		for (PS chunk : event.getChunks())
		{
			// Grab any regions in the chunk
			final List<ProtectedRegion> regions = this.getProtectedRegionsFor(chunk);
			
			// Ensure there are actually regions to go over 
			if (regions == null || regions.isEmpty()) continue;
			
			for (ProtectedRegion region : regions)
			{
				// Ensure it's not the global region, and check if they're a member
				if (region instanceof GlobalProtectedRegion || region.isMember(wrapperPlayer)) continue;
				
				// Check for a permission - can't use Perm enum for this 
				if (player.hasPermission("factions.allowregionclaim." + region.getId())) continue;
				
				// No permission, notify player and stop claiming
				mplayer.msg("<b>You cannot claim the chunk at %s, %s as there is a region in the way.", chunk.getChunkX(), chunk.getChunkZ());
				
				event.setCancelled(true);
				return;
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
		
		RegionManager regionManager = this.worldGuard.getRegionManager(ps.asBukkitWorld());
		
		String regionName = "factions_temp";
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionName, minChunk, maxChunk);
		
		Map<String, ProtectedRegion> regionMap = regionManager.getRegions(); 
		List<ProtectedRegion> regionList = new ArrayList<>(regionMap.values());
		
		// Let's find what we've overlapped
		List<ProtectedRegion> overlapRegions = region.getIntersectingRegions(regionList);
			
		return overlapRegions;
	}
	
}
