package com.massivecraft.factions.entity;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.util.AsciiCompass;
import com.massivecraft.massivecore.collections.MassiveMap;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.Txt;
import com.massivecraft.massivecore.xlib.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

public class Board extends Entity<Board> implements BoardInterface
{
	public static final transient Type MAP_TYPE = new TypeToken<Map<PS, TerritoryAccess>>(){}.getType();
	
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	public static Board get(Object oid)
	{
		return BoardColl.get().get(oid);
	}
	
	// -------------------------------------------- //
	// OVERRIDE: ENTITY
	// -------------------------------------------- //
	
	@Override
	public Board load(Board that)
	{
		this.map = that.map;
		
		return this;
	}
	
	@Override
	public boolean isDefault()
	{
		if (this.map == null) return true;
		if (this.map.isEmpty()) return true;
		return false;
	}
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	// TODO: Make TerritoryAccess immutable.
	
	private ConcurrentSkipListMap<PS, TerritoryAccess> map;
	public Map<PS, TerritoryAccess> getMap() { return Collections.unmodifiableMap(this.map); }
	public Map<PS, TerritoryAccess> getMapRaw() { return this.map; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public Board()
	{
		this.map = new ConcurrentSkipListMap<>();
	}
	
	public Board(Map<PS, TerritoryAccess> map)
	{
		this.map = new ConcurrentSkipListMap<>(map);
	}
	
	// -------------------------------------------- //
	// OVERRIDE: BOARD
	// -------------------------------------------- //
	
	// GET
	
	@Override
	public TerritoryAccess getTerritoryAccessAt(PS ps)
	{
		if (ps == null) return null;
		ps = ps.getChunkCoords(true);
		TerritoryAccess ret = this.map.get(ps);
		if (ret == null || ret.getHostFaction() == null) ret = TerritoryAccess.valueOf(Factions.ID_NONE);
		return ret;
	}
	
	@Override
	public Faction getFactionAt(PS ps)
	{
		if (ps == null) return null;
		TerritoryAccess ta = this.getTerritoryAccessAt(ps);
		return ta.getHostFaction();
	}
	
	// SET
	
	@Override
	public void setTerritoryAccessAt(PS ps, TerritoryAccess territoryAccess)
	{
		ps = ps.getChunkCoords(true);
		
		if (territoryAccess == null || (territoryAccess.getHostFactionId().equals(Factions.ID_NONE) && territoryAccess.isDefault()))
		{	
			this.map.remove(ps);
		}
		else
		{
			this.map.put(ps, territoryAccess);
		}
		
		this.changed();
	}
	
	@Override
	public void setFactionAt(PS ps, Faction faction)
	{
		TerritoryAccess territoryAccess = null;
		if (faction != null)
		{
			territoryAccess = TerritoryAccess.valueOf(faction.getId());
		}
		this.setTerritoryAccessAt(ps, territoryAccess);
	}
	
	// REMOVE
	
	@Override
	public void removeAt(PS ps)
	{
		this.setTerritoryAccessAt(ps, null);
	}
	
	@Override
	public void removeAll(Faction faction)
	{
		String factionId = faction.getId();
		
		for (Entry<PS, TerritoryAccess> entry : this.map.entrySet())
		{
			TerritoryAccess territoryAccess = entry.getValue();
			if ( ! territoryAccess.getHostFactionId().equals(factionId)) continue;
			
			PS ps = entry.getKey();
			this.removeAt(ps);
		}
	}
	
	// CHUNKS
	
	@Override
	public Set<PS> getChunks(Faction faction)
	{
		return this.getChunks(faction.getId());
	}
	
	@Override
	public Set<PS> getChunks(String factionId)
	{
		Set<PS> ret = new HashSet<>();
		for (Entry<PS, TerritoryAccess> entry : this.map.entrySet())
		{
			TerritoryAccess ta = entry.getValue();
			if (!ta.getHostFactionId().equals(factionId)) continue;
			
			PS ps = entry.getKey();
			ps = ps.withWorld(this.getId());
			ret.add(ps);
		}
		return ret;
	}
	
	@Override
	public Map<Faction, Set<PS>> getFactionToChunks()
	{
		Map<Faction, Set<PS>> ret = new MassiveMap<>();
		
		for (Entry<PS, TerritoryAccess> entry : this.map.entrySet())
		{
			// Get Faction
			TerritoryAccess ta = entry.getValue();
			Faction faction = ta.getHostFaction();
			if (faction == null) continue;
			
			// Get Chunks
			Set<PS> chunks = ret.get(faction);
			if (chunks == null)
			{
				chunks = new MassiveSet<>();
				ret.put(faction, chunks);
			}
			
			// Add Chunk
			PS chunk = entry.getKey();
			chunk = chunk.withWorld(this.getId());
			chunks.add(chunk);
		}
		
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
		for (TerritoryAccess ta : this.map.values())
		{
			if (!ta.getHostFactionId().equals(factionId)) continue;
			ret += 1;
		}
		return ret;
	}
	
	@Override
	public Map<Faction, Integer> getFactionToCount()
	{
		Map<Faction, Integer> ret = new MassiveMap<>();
		
		for (Entry<PS, TerritoryAccess> entry : this.map.entrySet())
		{
			// Get Faction
			TerritoryAccess ta = entry.getValue();
			Faction faction = ta.getHostFaction();
			if (faction == null) continue;
			
			// Get Count
			Integer count = ret.get(faction);
			if (count == null)
			{
				count = 0;
			}
			
			// Add Chunk
			ret.put(faction, count + 1);
		}
		
		return ret;
	}
	
	// CLAIMED
	
	@Override
	public boolean hasClaimed(Faction faction)
	{
		return this.hasClaimed(faction.getId());
	}
	
	@Override
	public boolean hasClaimed(String factionId)
	{
		for (TerritoryAccess ta : this.map.values())
		{
			if ( ! ta.getHostFactionId().equals(factionId)) continue;
			return true;
		}
		return false;
	}
	
	// NEARBY DETECTION
		
	// Is this coord NOT completely surrounded by coords claimed by the same faction?
	// Simpler: Is there any nearby coord with a faction other than the faction here?
	@Override
	public boolean isBorderPs(PS ps)
	{
		ps = ps.getChunk(true);
		
		PS nearby = null;
		Faction faction = this.getFactionAt(ps);
		
		nearby = ps.withChunkX(ps.getChunkX() +1);
		if (faction != this.getFactionAt(nearby)) return true;
		
		nearby = ps.withChunkX(ps.getChunkX() -1);
		if (faction != this.getFactionAt(nearby)) return true;
		
		nearby = ps.withChunkZ(ps.getChunkZ() +1);
		if (faction != this.getFactionAt(nearby)) return true;
		
		nearby = ps.withChunkZ(ps.getChunkZ() -1);
		if (faction != this.getFactionAt(nearby)) return true;
		
		return false;
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

	// Is this coord connected to any coord claimed by the specified faction?
	@Override
	public boolean isConnectedPs(PS ps, Faction faction)
	{
		ps = ps.getChunk(true);
		
		PS nearby = null;
		
		nearby = ps.withChunkX(ps.getChunkX() +1);
		if (faction == this.getFactionAt(nearby)) return true;
		
		nearby = ps.withChunkX(ps.getChunkX() -1);
		if (faction == this.getFactionAt(nearby)) return true;
		
		nearby = ps.withChunkZ(ps.getChunkZ() +1);
		if (faction == this.getFactionAt(nearby)) return true;
		
		nearby = ps.withChunkZ(ps.getChunkZ() -1);
		if (faction == this.getFactionAt(nearby)) return true;
		
		return false;
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
	public List<Object> getMap(RelationParticipator observer, PS centerPs, double inDegrees, int width, int height)
	{
		centerPs = centerPs.getChunkCoords(true);
		
		List<Object> ret = new ArrayList<>();
		Faction centerFaction = this.getFactionAt(centerPs);
		
		ret.add(Txt.titleize("(" + centerPs.getChunkX() + "," + centerPs.getChunkZ() + ") " + centerFaction.getName(observer)));
		
		int halfWidth = width / 2;
		int halfHeight = height / 2;
		width = halfWidth * 2 + 1;
		height = halfHeight * 2 + 1;
		
		PS topLeftPs = centerPs.plusChunkCoords(-halfWidth, -halfHeight);
		
		// Get the compass
		List<String> asciiCompass = AsciiCompass.getAsciiCompass(inDegrees);
		
		// Make room for the list of names
		height--;
		
		Map<Faction, Character> fList = new HashMap<>();
		int chrIdx = 0;
		boolean overflown = false;
		
		// For each row
		for (int dz = 0; dz < height; dz++)
		{
			// Draw and add that row
			StringBuilder row = new StringBuilder();
			for (int dx = 0; dx < width; dx++)
			{
				if (dx == halfWidth && dz == halfHeight)
				{
					row.append(Const.MAP_KEY_SEPARATOR);
					continue;
				}

				if ( ! overflown && chrIdx >= Const.MAP_KEY_CHARS.length) overflown = true;

				PS herePs = topLeftPs.plusChunkCoords(dx, dz);
				Faction hereFaction = this.getFactionAt(herePs);
				boolean contains = fList.containsKey(hereFaction);
				if (hereFaction.isNone())
				{
					row.append(Const.MAP_KEY_WILDERNESS);
				}
				else if ( ! contains && overflown)
				{
					row.append(Const.MAP_KEY_OVERFLOW);
				}
				else
				{
					if ( ! contains) fList.put(hereFaction, Const.MAP_KEY_CHARS[chrIdx++]);
					char fchar = fList.get(hereFaction);
					row.append(hereFaction.getColorTo(observer).toString()).append(fchar);
				}
			}
			
			String line = row.toString();
			
			// Add the compass
			if (dz == 0) line = asciiCompass.get(0) + line.substring(3*3);
			if (dz == 1) line = asciiCompass.get(1) + line.substring(3*3);
			if (dz == 2) line = asciiCompass.get(2) + line.substring(3*3);
			
			ret.add(line);
		}
			
		String fRow = "";
		for (Faction keyfaction : fList.keySet())
		{
			fRow += keyfaction.getColorTo(observer).toString() + fList.get(keyfaction) + ": " + keyfaction.getName() + " ";
		}
		if (overflown) fRow += Const.MAP_OVERFLOW_MESSAGE;
		fRow = fRow.trim();
		ret.add(fRow);
		
		return ret;
	}
	
}
