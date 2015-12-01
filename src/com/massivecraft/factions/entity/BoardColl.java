package com.massivecraft.factions.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.massivecore.collections.MassiveMap;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.Coll;
import com.massivecraft.massivecore.store.MStore;
import com.massivecraft.massivecore.util.MUtil;

public class BoardColl extends Coll<Board> implements BoardInterface
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static BoardColl i = new BoardColl();
	public static BoardColl get() { return i; }
	private BoardColl()
	{
		super(Const.COLLECTION_BOARD, Board.class, MStore.getDb(), Factions.get());
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
	
	@Override
	public void clean()
	{
		for (Board board : this.getAll())
		{
			board.clean();
		}
	}
	
	// CHUNKS
	
	@Override
	public Set<PS> getChunks(Faction faction)
	{
		Set<PS> ret = new HashSet<PS>();
		for (Board board : this.getAll())
		{
			ret.addAll(board.getChunks(faction));
		}
		return ret;
	}
	
	@Override
	public Set<PS> getChunks(String factionId)
	{
		Set<PS> ret = new HashSet<PS>();
		for (Board board : this.getAll())
		{
			ret.addAll(board.getChunks(factionId));
		}
		return ret;
	}
	
	@Override
	public Map<Faction, Set<PS>> getFactionToChunks()
	{
		Map<Faction, Set<PS>> ret = null;
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
		
		if (ret == null) ret = new MassiveMap<Faction, Set<PS>>();
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
		
		if (ret == null) ret = new MassiveMap<Faction, Integer>();
		return ret;
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
	
	// MAP GENERATION
	
	@Override
	public ArrayList<String> getMap(RelationParticipator observer, PS centerPs, double inDegrees, int width, int height)
	{
		if (centerPs == null) return null;
		Board board = this.get(centerPs.getWorld());
		if (board == null) return null;
		return board.getMap(observer, centerPs, inDegrees, width, height);
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	// Distance -1 returns 0 chunks always.
	// Distance 0 returns 1 chunk only (the one supplied).
	// Distance 1 returns 3x3 = 9 chunks.
	public static Set<PS> getNearbyChunks(PS chunk, int distance)
	{
		// Fix Args
		if (chunk == null) throw new NullPointerException("chunk");
		chunk = chunk.getChunk(true);
		
		// Create Ret
		Set<PS> ret = new LinkedHashSet<PS>();
		
		if (distance < 0) return ret;
		
		// Main
		int xmin = chunk.getChunkX() - distance;
		int xmax = chunk.getChunkX() + distance;
		
		int zmin = chunk.getChunkZ() - distance;
		int zmax = chunk.getChunkZ() + distance;
		
		for (int x = xmin; x <= xmax; x++)
		{
			for (int z = zmin; z <= zmax; z++)
			{
				ret.add(chunk.withChunkX(x).withChunkZ(z));
			}
		}
		
		// Return Ret
		return ret;
	}
	
	public static Set<PS> getNearbyChunks(Collection<PS> chunks, int distance)
	{
		// Fix Args
		if (chunks == null) throw new NullPointerException("chunks");
		
		// Create Ret
		Set<PS> ret = new LinkedHashSet<PS>();
		
		if (distance < 0) return ret;
		
		// Main
		for (PS chunk : chunks)
		{
			ret.addAll(getNearbyChunks(chunk, distance));
		}
		
		// Return Ret
		return ret;
	}
	
	public static Set<Faction> getDistinctFactions(Collection<PS> chunks)
	{
		// Fix Args
		if (chunks == null) throw new NullPointerException("chunks");
		
		// Create Ret
		Set<Faction> ret = new LinkedHashSet<Faction>();
		
		// Main
		for (PS chunk : chunks)
		{
			Faction faction = BoardColl.get().getFactionAt(chunk);
			if (faction == null) continue;
			ret.add(faction);
		}
		
		// Return Ret
		return ret;
	}
	
	public static Map<PS, Faction> getChunkFaction(Collection<PS> chunks)
	{
		Map<PS, Faction> ret = new LinkedHashMap<PS, Faction>();
		
		for (PS chunk : chunks)
		{
			chunk = chunk.getChunk(true);
			Faction faction = get().getFactionAt(chunk);
			if (faction == null) faction = FactionColl.get().getNone();
			ret.put(chunk, faction);
		}
		
		return ret;
	}
	
}
