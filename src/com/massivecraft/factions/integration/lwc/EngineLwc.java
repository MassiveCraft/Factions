package com.massivecraft.factions.integration.lwc;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.griefcraft.sql.PhysDB;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.factions.event.EventFactionsChunksChange;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.IdUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class EngineLwc extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineLwc i = new EngineLwc();
	public static EngineLwc get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void setActiveInner(boolean active)
	{
		if (active)
		{
			LWC.getInstance().getModuleLoader().registerModule(Factions.get(), new FactionsLwcModule(Factions.get()));
		}
		else
		{
			LWC.getInstance().getModuleLoader().removeModules(Factions.get());
		}
	}
	
	// -------------------------------------------- //
	// LISTENER
	// -------------------------------------------- //
	
	public void removeProtectionsOnChunkChange(Faction newFaction, EventFactionsChunkChangeType type, Set<PS> chunks)
	{
		// If we are supposed to clear at this chunk change type ...
		Boolean remove = MConf.get().lwcRemoveOnChange.get(type);
		if (remove == null) return;
		if (remove == false) return;
		
		// ... then remove for all other factions than the new one.
		// First we wait one tick to make sure the chunk ownership changes have been applied.
		// Then we remove the protections but we do it asynchronously to not lock the main thread.
		for (PS chunk : chunks)
		{
			removeAlienProtectionsAsyncNextTick(chunk, newFaction);
		}
	}
	
	public void removeProtectionsOnChunkChange(Faction newFaction, Map<EventFactionsChunkChangeType, Set<PS>> typeChunks)
	{
		for (Entry<EventFactionsChunkChangeType, Set<PS>> typeChunk : typeChunks.entrySet())
		{
			final EventFactionsChunkChangeType type = typeChunk.getKey();
			final Set<PS> chunks = typeChunk.getValue();
			removeProtectionsOnChunkChange(newFaction, type, chunks);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void removeProtectionsOnChunkChange(EventFactionsChunksChange event)
	{
		removeProtectionsOnChunkChange(event.getNewFaction(), event.getTypeChunks());
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	// This method causes LWC to run an SQL query which can take a few milliseconds.
	// For that reason this method should not be executed in the main server thread.
	// After looking through the source code of LWC I am also hopeful this is thread safe. 
	public static List<Protection> getProtectionsInChunk(PS chunkPs)
	{
		final int xmin = chunkPs.getChunkX() * 16;
		final int xmax = xmin + 15;
		
		final int ymin = 0;
		final int ymax = 255;
		
		final int zmin = chunkPs.getChunkZ() * 16;
		final int zmax = zmin + 15;
		
		PhysDB db = LWC.getInstance().getPhysicalDatabase();
		return db.loadProtections(chunkPs.getWorld(), xmin, xmax, ymin, ymax, zmin, zmax);
	}
	
	// As with the method above: Thread safe and slow. Do run asynchronously.
	public static void removeAlienProtectionsRaw(PS chunkPs, Faction faction)
	{
		List<MPlayer> nonAliens = faction.getMPlayers();
		for (Protection protection : getProtectionsInChunk(chunkPs))
		{
			// NOTE: The LWC protection owner is still the name and not the UUID. For that reason we must convert it. 
			String ownerName = protection.getOwner();
			String ownerId = IdUtil.getId(ownerName);
			MPlayer owner = MPlayer.get(ownerId);
			if (nonAliens.contains(owner)) continue;
			protection.remove();
		}
	}
	
	public static void removeAlienProtectionsAsync(final PS chunkPs, final Faction faction)
	{
		Bukkit.getScheduler().runTaskAsynchronously(Factions.get(), new Runnable()
		{
			@Override
			public void run()
			{
				removeAlienProtectionsRaw(chunkPs, faction);
			}
		});
	}
	
	public static void removeAlienProtectionsAsyncNextTick(final PS chunkPs, final Faction faction)
	{
		Bukkit.getScheduler().runTaskLater(Factions.get(), new Runnable()
		{
			@Override
			public void run()
			{
				removeAlienProtectionsAsync(chunkPs, faction);
			}
		}, 0);
	}
	
}
