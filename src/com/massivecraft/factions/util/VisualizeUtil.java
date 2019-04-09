package com.massivecraft.factions.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

// TODO: Only send blocks in visual range
// TODO: Only send blocks that where changed when clearing?
// TODO: Create packed queue to avoid freezes.

public class VisualizeUtil
{
	protected static Map<UUID, Set<Location>> playerLocations = new HashMap<>();
	public static Set<Location> getPlayerLocations(Player player)
	{
		return getPlayerLocations(player.getUniqueId());
	}
	public static Set<Location> getPlayerLocations(UUID uuid)
	{
		Set<Location> ret = playerLocations.get(uuid);
		if (ret == null)
		{
			ret = new HashSet<>();
			playerLocations.put(uuid, ret);
		}
		return ret;
	}
	
	// -------------------------------------------- //
	// SINGLE
	// -------------------------------------------- //
	
	@SuppressWarnings("deprecation")
	public static void addLocation(Player player, Location location, Material type, byte data)
	{
		getPlayerLocations(player).add(location);
		player.sendBlockChange(location, type, data);
	}
	
	@SuppressWarnings("deprecation")
	public static void addLocation(Player player, Location location, Material type)
	{
		getPlayerLocations(player).add(location);
		player.sendBlockChange(location, type, (byte) 0);
	}
	
	// -------------------------------------------- //
	// MANY
	// -------------------------------------------- //
	
	@SuppressWarnings("deprecation")
	public static void addLocations(Player player, Map<Location, Material> locationMaterials)
	{
		Set<Location> ploc = getPlayerLocations(player);
		for (Entry<Location, Material> entry : locationMaterials.entrySet())
		{
			ploc.add(entry.getKey());
			player.sendBlockChange(entry.getKey(), entry.getValue(), (byte) 0);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void addLocations(Player player, Collection<Location> locations, Material type)
	{
		Set<Location> ploc = getPlayerLocations(player);
		for (Location location : locations)
		{
			ploc.add(location);
			player.sendBlockChange(location, type, (byte) 0);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void addBlocks(Player player, Collection<Block> blocks, Material type)
	{
		Set<Location> ploc = getPlayerLocations(player);
		for (Block block : blocks)
		{
			Location location = block.getLocation();
			ploc.add(location);
			player.sendBlockChange(location, type, (byte) 0);
		}
	}
	
	// -------------------------------------------- //
	// CLEAR
	// -------------------------------------------- //
	
	@SuppressWarnings("deprecation")
	public static void clear(Player player)
	{
		Set<Location> locations = getPlayerLocations(player);
		if (locations == null) return;
		for (Location location : locations)
		{
			Block block = location.getWorld().getBlockAt(location);
			player.sendBlockChange(location, block.getType(), block.getData());
		}
		locations.clear();
	}
	
}
