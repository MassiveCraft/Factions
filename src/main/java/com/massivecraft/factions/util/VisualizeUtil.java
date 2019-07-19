package com.massivecraft.factions.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

public class VisualizeUtil {

    protected static Map<UUID, Set<Location>> playerLocations = new HashMap<>();

    public static Set<Location> getPlayerLocations(Player player) {
        return getPlayerLocations(player.getUniqueId());
    }

    public static Set<Location> getPlayerLocations(UUID uuid) {
        return playerLocations.computeIfAbsent(uuid, k -> new HashSet<>());
    }

    @SuppressWarnings("deprecation")
    public static void addLocation(Player player, Location location, Material material) {
        getPlayerLocations(player).add(location);
        player.sendBlockChange(location, material, (byte) 0);
    }

    @SuppressWarnings("deprecation")
    public static void addLocations(Player player, Map<Location, Material> locationMaterialIds) {
        Set<Location> ploc = getPlayerLocations(player);
        for (Entry<Location, Material> entry : locationMaterialIds.entrySet()) {
            ploc.add(entry.getKey());
            player.sendBlockChange(entry.getKey(), entry.getValue(), (byte) 0);
        }
    }

    @SuppressWarnings("deprecation")
    public static void addLocations(Player player, Collection<Location> locations, Material material) {
        Set<Location> ploc = getPlayerLocations(player);
        for (Location location : locations) {
            ploc.add(location);
            player.sendBlockChange(location, material, (byte) 0);
        }
    }

    @SuppressWarnings("deprecation")
    public static void addBlocks(Player player, Collection<Block> blocks, Material material) {
        Set<Location> ploc = getPlayerLocations(player);
        for (Block block : blocks) {
            Location location = block.getLocation();
            ploc.add(location);
            player.sendBlockChange(location, material, (byte) 0);
        }
    }

    @SuppressWarnings("deprecation")
    public static void clear(Player player) {
        Set<Location> locations = getPlayerLocations(player);
        if (locations == null) {
            return;
        }
        for (Location location : locations) {
            Block block = location.getWorld().getBlockAt(location);
            player.sendBlockChange(location, block.getType(), block.getData());
        }
        locations.clear();
    }

}
