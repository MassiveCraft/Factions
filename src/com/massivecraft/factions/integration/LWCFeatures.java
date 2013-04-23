package com.massivecraft.factions.integration;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.plugin.Plugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.mcore.ps.PS;

public class LWCFeatures
{
	private static LWC lwc;

	public static void setup()
	{
		Plugin test = Bukkit.getServer().getPluginManager().getPlugin("LWC");
		if(test == null || !test.isEnabled()) return;

		lwc = ((LWCPlugin)test).getLWC();
		Factions.get().log("Successfully hooked into LWC!");
	}

	public static boolean getEnabled()
	{
		return lwc != null;
	}

	public static void clearAllProtections(PS chunkPs)
	{
		for (Protection protection : getProtectionsInChunk(chunkPs))
		{
			protection.remove();
		}
	}

	public static void clearOtherProtections(PS chunkPs, Faction faction)
	{
		for (Protection protection : getProtectionsInChunk(chunkPs))
		{
			UPlayer owner = UPlayer.get(protection.getOwner());
			if (faction.getUPlayers().contains(owner)) continue;
			protection.remove();
		}
	}

	public static List<Protection> getProtectionsInChunk(PS chunkPs)
	{
		List<Protection> ret = new ArrayList<Protection>();

		// Get the chunk
		Chunk chunk = null;
		try
		{
			chunk = chunkPs.asBukkitChunk(true);
		}
		catch (Exception e)
		{
			return ret;
		}

		for (BlockState blockState : chunk.getTileEntities())
		{
			// TODO: Can something else be protected by LWC? Or is it really only chests?
			// TODO: How about we run through each block in the chunk just to be on the safe side?
			if (blockState.getType() != Material.CHEST) continue;
			Block block = blockState.getBlock();

			Protection protection = lwc.findProtection(block);
			if (protection == null) continue;

			ret.add(protection);
		}

		return ret;
	}

}
