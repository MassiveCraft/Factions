package com.massivecraft.factions.integration;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.P;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Worldguard {

    private static boolean enabled = false;

    public static void init(Plugin plugin) {
        Plugin wgplug = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        if (wgplug == null) {
            enabled = false;
            P.p.log("Could not hook to WorldGuard. WorldGuard checks are disabled.");
        } else {
            enabled = true;
            P.p.log("Successfully hooked to WorldGuard.");
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
        if (!enabled) {
            // No WG hooks so we'll always bypass this check.
            return true;
        }

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        return query.testState(localPlayer.getLocation(), localPlayer, Flags.PVP);
    }

    // Check if player can build at location by worldguards rules.
    // Returns:
    //	True: Player can build in the region.
    //	False: Player can not build in the region.
    public static boolean playerCanBuild(Player player, Location loc) {
        if (!enabled) {
            // No WG hooks so we'll always bypass this check.
            return false;
        }

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        return query.testBuild(localPlayer.getLocation(), localPlayer);
    }

    public static boolean checkForRegionsInChunk(FLocation flocation) {
        if (!enabled) {
            // No WG hooks so we'll always bypass this check.
            return false;
        }

        Chunk chunk = flocation.getChunk();

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(chunk.getWorld()));
        if (regions == null){
            return false;
        }

        World world = chunk.getWorld();
        int minChunkX = chunk.getX() << 4;
        int minChunkZ = chunk.getZ() << 4;
        int maxChunkX = minChunkX + 15;
        int maxChunkZ = minChunkZ + 15;

        int worldHeight = world.getMaxHeight(); // Allow for heights other than default

        BlockVector3 min = BlockVector3.at(minChunkX, 0, minChunkZ);
        BlockVector3 max = BlockVector3.at(maxChunkX, worldHeight, maxChunkZ);
        ProtectedRegion region = new ProtectedCuboidRegion("wgregionflagcheckforfactions", min, max);
        ApplicableRegionSet set = regions.getApplicableRegions(region);

        return set.size() > 1;
    }
}