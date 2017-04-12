package com.massivecraft.factions.util;

import org.bukkit.Location;
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
	public static void addLocation(Player player, Location location, int typeId, byte data)
	{
		getPlayerLocations(player).add(location);
		player.sendBlockChange(location, typeId, data);
	}
	
	@SuppressWarnings("deprecation")
	public static void addLocation(Player player, Location location, int typeId)
	{
		getPlayerLocations(player).add(location);
		player.sendBlockChange(location, typeId, (byte) 0);
	}
	
	// -------------------------------------------- //
	// MANY
	// -------------------------------------------- //
	
	@SuppressWarnings("deprecation")
	public static void addLocations(Player player, Map<Location, Integer> locationMaterialIds)
	{
		Set<Location> ploc = getPlayerLocations(player);
		for (Entry<Location, Integer> entry : locationMaterialIds.entrySet())
		{
			ploc.add(entry.getKey());
			player.sendBlockChange(entry.getKey(), entry.getValue(), (byte) 0);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void addLocations(Player player, Collection<Location> locations, int typeId)
	{
		Set<Location> ploc = getPlayerLocations(player);
		for (Location location : locations)
		{
			ploc.add(location);
			player.sendBlockChange(location, typeId, (byte) 0);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void addBlocks(Player player, Collection<Block> blocks, int typeId)
	{
		Set<Location> ploc = getPlayerLocations(player);
		for (Block block : blocks)
		{
			Location location = block.getLocation();
			ploc.add(location);
			player.sendBlockChange(location, typeId, (byte) 0);
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
			player.sendBlockChange(location, block.getTypeId(), block.getData());
		}
		locations.clear();
	}
	
}
