package com.massivecraft.factions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.UnsupportedIntersectionException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import static com.sk89q.worldguard.bukkit.BukkitUtil.*;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.BlockVector;

import org.bukkit.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

/*
 *  Worldguard Region Checking
 *  Author: Spathizilla
 */

public class Worldguard {
	private static WorldGuardPlugin wg;
	private static boolean enabled = false;
	
	public static void init(Plugin plugin) {
		Plugin wgplug = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		if (wgplug == null || !(wgplug instanceof WorldGuardPlugin)) {
			enabled = false;
			wg = null;
			System.out.println("[Factions] Could not hook to WorldGuard. WorldGuard checks are disabled.");
	    } else {
	    	wg = (WorldGuardPlugin) wgplug;
	    	enabled = true;
	    	System.out.println("[Factions] Successfully hooked to WorldGuard.");
	    }
	}

	public static boolean isEnabled() {
		return enabled;
	}

	// PVP Flag check 
	// Returns:
	//   True: PVP is allowed
	//   False: PVP is disallowed
	public static boolean isPVP(Player player) {
		if(isEnabled()) {
			Location loc = player.getLocation();
			World world = loc.getWorld();
			Vector pt = toVector(loc);

			RegionManager regionManager = wg.getRegionManager(world);
			ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
			return set.allows(DefaultFlag.PVP);
		} else {
			// No WG hooks so we'll always bypass this check.
			return true;
		}
	}
	
	// Check for Regions in chunk the chunk
	// Returns:
	//   True: Regions found within chunk
	//   False: No regions found within chunk
	public static boolean checkForRegionsInChunk(Location loc) {
		int plocX = loc.getBlockX();
		int plocZ = loc.getBlockZ();
		World world = loc.getWorld();
		
		Chunk chunk = world.getChunkAt(plocX, plocZ);
		int chunkX = chunk.getX();
		int chunkZ = chunk.getZ();
		
		BlockVector minChunk = new BlockVector(chunkX, 0, chunkZ);
		BlockVector maxChunk = new BlockVector(chunkX+15, 128, chunkZ+15);
		
		RegionManager regionManager = wg.getRegionManager(world);
		ProtectedCuboidRegion region = new ProtectedCuboidRegion("wgfactionoverlapcheck", minChunk, maxChunk);
		Map<String, ProtectedRegion> allregions = regionManager.getRegions(); 
		
		List<ProtectedRegion> allregionslist = new ArrayList<ProtectedRegion>(allregions.values());
		List<ProtectedRegion> overlaps;
		boolean foundregions = false;

		try {
			overlaps = region.getIntersectingRegions(allregionslist);
			if(overlaps.isEmpty() || overlaps == null) {
				foundregions = false;
			} else {
				foundregions = true;
			}
		} catch (UnsupportedIntersectionException e) {
			e.printStackTrace();
		}

		region = null;
		allregionslist = null;
		overlaps = null;
		
		return foundregions;
	}
}