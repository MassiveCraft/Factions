package com.massivecraft.factions.integration;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;

public class LWCFeatures 
{

	private static LWC lwc;
	private static boolean isEnabled = false;
	
	public static void integrateLWC(LWCPlugin test)
	{
		lwc = test.getLWC();
		isEnabled = true;
		
		P.p.log("Successfully hooked into LWC!");
	}

	public static void clearOtherChests(FLocation flocation, Faction faction)
	{
		Location location = new Location(Bukkit.getWorld(flocation.getWorldName()), flocation.getX() * 16, 5, flocation.getZ() * 16);
		Chunk chunk = location.getChunk();
		BlockState[] blocks = chunk.getTileEntities();
		List<Block> chests = new LinkedList<Block>();
		
		for(int x = 0; x < blocks.length; x++)
		{
			if(blocks[x].getType() == Material.CHEST)
			{
				chests.add(blocks[x].getBlock());
			}
		}
		
		for(int x = 0; x < chests.size(); x++)
		{
			if(lwc.findProtection(chests.get(x)) != null)
			{
				if(!faction.getFPlayers().contains(FPlayers.i.get(lwc.findProtection(chests.get(x)).getBukkitOwner())))
					lwc.findProtection(chests.get(x)).remove();
			}
		}
	}
	
	public static void clearAllChests(FLocation flocation)
	{
		Location location = new Location(Bukkit.getWorld(flocation.getWorldName()), flocation.getX() * 16, 5, flocation.getZ() * 16);
		Chunk chunk = location.getChunk();
		BlockState[] blocks = chunk.getTileEntities();
		List<Block> chests = new LinkedList<Block>();
		
		for(int x = 0; x < blocks.length; x++)
		{
			if(blocks[x].getType() == Material.CHEST)
			{
				chests.add(blocks[x].getBlock());
			}
		}
		
		for(int x = 0; x < chests.size(); x++)
		{
			if(lwc.findProtection(chests.get(x)) != null)
			{
					lwc.findProtection(chests.get(x)).remove();
			}
		}
	}

	public static boolean getEnabled()
	{
		return isEnabled;
	}
}
