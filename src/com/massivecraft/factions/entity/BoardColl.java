package com.massivecraft.factions.entity;

import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.massivecore.collections.MassiveMap;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.Coll;
import com.massivecraft.massivecore.util.MUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class BoardColl extends Coll<Board> implements BoardInterface
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static BoardColl i = new BoardColl();
	public static BoardColl get() { return i; }
	private BoardColl()
	{
		this.setCreative(true);
		this.setLowercasing(true);
	}

	// -------------------------------------------- //
	// STACK TRACEABILITY
	// -------------------------------------------- //
	
	@Override
	public void onTick()
	{
		super.onTick();
	}
	
	// -------------------------------------------- //
	// OVERRIDE: COLL
	// -------------------------------------------- //
	
	@Override
	public String fixId(Object oid)
	{
		if (oid == null) return null;
		if (oid instanceof String) return (String)oid;
		if (oid instanceof Board) return ((Board)oid).getId();
		
		return MUtil.extract(String.class, "worldName", oid);
	}
	
	// -------------------------------------------- //
	// OVERRIDE: BOARD
	// -------------------------------------------- //
	
	@Override
	public TerritoryAccess getTerritoryAccessAt(PS ps)
	{
		if (ps == null) return null;
		Board board = this.get(ps.getWorld());
		if (board == null) return null;
		return board.getTerritoryAccessAt(ps);
	}
	
	@Override
	public Faction getFactionAt(PS ps)
	{
		if (ps == null) return null;
		Board board = this.get(ps.getWorld());
		if (board == null) return null;
		return board.getFactionAt(ps);
	}
	
	// SET

	@Override
	public void setTerritoryAccessAt(PS ps, TerritoryAccess territoryAccess)
	{
		if (ps == null) return;
		Board board = this.get(ps.getWorld());
		if (board == null) return;
		board.setTerritoryAccessAt(ps, territoryAccess);
	}
	
	@Override
	public void setFactionAt(PS ps, Faction faction)
	{
		if (ps == null) return;
		Board board = this.get(ps.getWorld());
		if (board == null) return;
		board.setFactionAt(ps, faction);
	}
	
	// REMOVE
	
	@Override
	public void removeAt(PS ps)
	{
		if (ps == null) return;
		Board board = this.get(ps.getWorld());
		if (board == null) return;
		board.removeAt(ps);
	}
	
	@Override
	public void removeAll(Faction faction)
	{
		for (Board board : this.getAll())
		{
			board.removeAll(faction);
		}
	}
	
	// CHUNKS
	
	@Override
	public Set<PS> getChunks(Faction faction)
	{
		// Create
		Set<PS> ret = new HashSet<>();
		
		// Fill
		for (Board board : this.getAll())
		{
			ret.addAll(board.getChunks(faction));
		}
		
		// Return
		return ret;
	}
	
	@Override
	public Set<PS> getChunks(String factionId)
	{
		// Create
		Set<PS> ret = new HashSet<>();
		
		// Fill
		for (Board board : this.getAll())
		{
			ret.addAll(board.getChunks(factionId));
		}
		
		// Return
		return ret;
	}
	
	@Override
	public Map<Faction, Set<PS>> getFactionToChunks()
	{
		// Create
		Map<Faction, Set<PS>> ret = null;
		
		// Fill
		for (Board board : this.getAll())
		{
			// Use the first board directly
			Map<Faction, Set<PS>> factionToChunks = board.getFactionToChunks();
			if (ret == null)
			{
				ret = factionToChunks;
				continue;
			}
			
			// Merge the following boards
			for (Entry<Faction, Set<PS>> entry : factionToChunks.entrySet())
			{
				Faction faction = entry.getKey();
				Set<PS> chunks = ret.get(faction);
				if (chunks == null)
				{
					ret.put(faction, entry.getValue());
				}
				else
				{
					chunks.addAll(entry.getValue());
				}
			}
		}
		
		// Enforce create
		if (ret == null) ret = new MassiveMap<>();
		
		// Return
		return ret;
	}
	
	// COUNT
	
	@Override
	public int getCount(Faction faction)
	{
		return this.getCount(faction.getId());
	}
	
	@Override
	public int getCount(String factionId)
	{
		int ret = 0;
		for (Board board : this.getAll())
		{
			ret += board.getCount(factionId);
		}
		return ret;
	}
	
	@Override
	public Map<Faction, Integer> getFactionToCount()
	{
		Map<Faction, Integer> ret = null;
		for (Board board : this.getAll())
		{
			// Use the first board directly
			Map<Faction, Integer> factionToCount = board.getFactionToCount();
			if (ret == null)
			{
				ret = factionToCount;
				continue;
			}
			
			// Merge the following boards
			for (Entry<Faction, Integer> entry : factionToCount.entrySet())
			{
				Faction faction = entry.getKey();
				Integer count = ret.get(faction);
				if (count == null)
				{
					ret.put(faction, entry.getValue());
				}
				else
				{
					ret.put(faction, count + entry.getValue());
				}
			}
		}
		
		if (ret == null) ret = new MassiveMap<>();
		return ret;
	}
	
	// COUNT
	
	@Override
	public boolean hasClaimed(Faction faction)
	{
		return this.hasClaimed(faction.getId());
	}
	
	@Override
	public boolean hasClaimed(String factionId)
	{
		for (Board board : this.getAll())
		{
			if (board.hasClaimed(factionId)) return true;
		}
		return false;
	}
	
	// NEARBY DETECTION
	
	@Override
	public boolean isBorderPs(PS ps)
	{
		if (ps == null) return false;
		Board board = this.get(ps.getWorld());
		if (board == null) return false;
		return board.isBorderPs(ps);
	}

	@Override
	public boolean isAnyBorderPs(Set<PS> pss)
	{
		for (PS ps : pss)
		{
			if (this.isBorderPs(ps)) return true;
		}
		return false;
	}
	
	@Override
	public boolean isConnectedPs(PS ps, Faction faction)
	{
		if (ps == null) return false;
		Board board = this.get(ps.getWorld());
		if (board == null) return false;
		return board.isConnectedPs(ps, faction);
	}
	
	@Override
	public boolean isAnyConnectedPs(Set<PS> pss, Faction faction)
	{
		for (PS ps : pss)
		{
			if (this.isConnectedPs(ps, faction)) return true;
		}
		return false;
	}
	
	// -------------------------------------------- //
	// WORLDS
	// -------------------------------------------- //
	
	public Set<String> getClaimedWorlds(Faction faction)
	{
		return getClaimedWorlds(faction.getId());
	}
	
	public Set<String> getClaimedWorlds(String factionId)
	{
		// Create
		Set<String> ret = new MassiveSet<>();
		
		// Fill
		for (Board board : this.getAll())
		{
			if (board.hasClaimed(factionId)) ret.add(board.getId());
		}
		
		// Return
		return ret;
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	// Distance -1 returns 0 chunks always.
	// Distance 0 returns 1 chunk only (the one supplied).
	// Distance 1 returns 3x3 = 9 chunks.
	public static Set<PS> getNearbyChunks(PS psChunk, int distance)
	{
		// Fix Args
		if (psChunk == null) throw new NullPointerException("psChunk");
		psChunk = psChunk.getChunk(true);
		
		// Create
		Set<PS> ret = new LinkedHashSet<>();
		if (distance < 0) return ret;
		
		// Fill
		int chunkX = psChunk.getChunkX();
		int xmin = chunkX - distance;
		int xmax = chunkX + distance;
		
		int chunkZ = psChunk.getChunkZ();
		int zmin = chunkZ - distance;
		int zmax = chunkZ + distance;
		
		for (int x = xmin; x <= xmax; x++)
		{
			PS psChunkX = psChunk.withChunkX(x);
			for (int z = zmin; z <= zmax; z++)
			{
				ret.add(psChunkX.withChunkZ(z));
			}
		}
		
		// Return
		return ret;
	}
	
	public static Set<PS> getNearbyChunks(Collection<PS> chunks, int distance)
	{
		// Fix Args
		if (chunks == null) throw new NullPointerException("chunks");
		
		// Create
		Set<PS> ret = new LinkedHashSet<>();
		
		if (distance < 0) return ret;
		
		// Fill
		for (PS chunk : chunks)
		{
			ret.addAll(getNearbyChunks(chunk, distance));
		}
		
		// Return
		return ret;
	}
	
	public static Set<Faction> getDistinctFactions(Collection<PS> chunks)
	{
		// Fix Args
		if (chunks == null) throw new NullPointerException("chunks");
		
		// Create
		Set<Faction> ret = new LinkedHashSet<>();
		
		// Fill
		for (PS chunk : chunks)
		{
			Faction faction = get().getFactionAt(chunk);
			if (faction == null) continue;
			ret.add(faction);
		}
		
		// Return
		return ret;
	}
	
	public static Map<PS, Faction> getChunkFaction(Collection<PS> chunks)
	{
		// Create
		Map<PS, Faction> ret = new LinkedHashMap<>();
		
		// Fill
		Faction none = FactionColl.get().getNone();
		for (PS chunk : chunks)
		{
			chunk = chunk.getChunk(true);
			Faction faction = get().getFactionAt(chunk);
			if (faction == null) faction = none;
			ret.put(chunk, faction);
		}
		
		// Return
		return ret;
	}
	
}
