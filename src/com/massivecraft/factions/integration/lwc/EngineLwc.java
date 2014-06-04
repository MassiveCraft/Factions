package com.massivecraft.factions.integration.lwc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.event.EventFactionsChunkChange;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.massivecore.ps.PS;


public class EngineLwc implements Listener
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineLwc i = new EngineLwc();
	public static EngineLwc get() { return i; }
	private EngineLwc() {}
	
	// -------------------------------------------- //
	// ACTIVATE & DEACTIVATE
	// -------------------------------------------- //
	
	public void activate()
	{
		Bukkit.getPluginManager().registerEvents(this, Factions.get());
	}
	
	public void deactivate()
	{
		HandlerList.unregisterAll(this);
	}
	
	// -------------------------------------------- //
	// LISTENER
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void removeProtectionsOnChunkChange(EventFactionsChunkChange event)
	{
		// If we are supposed to clear at this chunk change type ...
		Faction newFaction = event.getNewFaction();
		UConf uconf = UConf.get(newFaction);
		EventFactionsChunkChangeType type = event.getType();
		Boolean remove = uconf.lwcRemoveOnChange.get(type);
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
		List<UPlayer> nonAliens = faction.getUPlayers();
		for (Protection protection : getProtectionsInChunk(chunkPs))
		{
			UPlayer owner = UPlayer.get(protection.getOwner());
			if (nonAliens.contains(owner)) continue;
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
			
			Protection protection = LWC.getInstance().findProtection(block);
			if (protection == null) continue;
			
			ret.add(protection);
		}
		
		return ret;
	}
	
}
