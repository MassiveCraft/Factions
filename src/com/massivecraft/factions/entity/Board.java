package com.massivecraft.factions.entity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.util.AsciiCompass;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.Txt;
import com.massivecraft.massivecore.xlib.gson.reflect.TypeToken;

public class Board extends Entity<Board> implements BoardInterface
{
	public static final transient Type MAP_TYPE = new TypeToken<Map<PS, TerritoryAccess>>(){}.getType();
	
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	public static Board get(Object oid)
	{
		return BoardColls.get().get2(oid);
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
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public Board()
	{
		this.map = new ConcurrentSkipListMap<PS, TerritoryAccess>();
	}
	
	public Board(Map<PS, TerritoryAccess> map)
	{
		this.map = new ConcurrentSkipListMap<PS, TerritoryAccess>(map);
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
		if (ret == null) ret = TerritoryAccess.valueOf(UConf.get(this).factionIdNone);
		return ret;
	}
	
	@Override
	public Faction getFactionAt(PS ps)
	{
		if (ps == null) return null;
		TerritoryAccess ta = this.getTerritoryAccessAt(ps);
		return ta.getHostFaction(this);
	}
	
	// SET
	
	@Override
	public void setTerritoryAccessAt(PS ps, TerritoryAccess territoryAccess)
	{
		ps = ps.getChunkCoords(true);
		
		if (territoryAccess == null || (territoryAccess.getHostFactionId().equals(UConf.get(this).factionIdNone) && territoryAccess.isDefault()))
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
	
	// Removes orphaned foreign keys
	@Override
	public void clean()
	{
		FactionColl factionColl = FactionColls.get().get(this);
		
		for (Entry<PS, TerritoryAccess> entry : this.map.entrySet())
		{
			TerritoryAccess territoryAccess = entry.getValue();
			String factionId = territoryAccess.getHostFactionId();
			if (factionColl.containsId(factionId)) continue;
			
			PS ps = entry.getKey();
			this.removeAt(ps);
			
			Factions.get().log("Board cleaner removed "+factionId+" from "+ps);
		}
	}
	
	// CHUNKS
	
	@Override
	public Set<PS> getChunks(Faction faction)
	{
		return this.getChunks(faction.getId());
	}
	
	public Set<PS> getChunks(String factionId)
	{
		Set<PS> ret = new HashSet<PS>();
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
	
	// COUNT
	
	@Override
	public int getCount(Faction faction)
	{
		return this.getCount(faction.getId());
	}
	
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
	
	// MAP GENERATION
	
	@Override
	public ArrayList<String> getMap(RelationParticipator observer, PS centerPs, double inDegrees)
	{
		centerPs = centerPs.getChunkCoords(true);
		
		ArrayList<String> ret = new ArrayList<String>();
		Faction centerFaction = this.getFactionAt(centerPs);
		
		ret.add(Txt.titleize("("+centerPs.getChunkX() + "," + centerPs.getChunkZ()+") "+centerFaction.getName(observer)));
		
		int halfWidth = Const.MAP_WIDTH / 2;
		int halfHeight = Const.MAP_HEIGHT / 2;
		
		PS topLeftPs = centerPs.plusChunkCoords(-halfWidth, -halfHeight);
		
		int width = halfWidth * 2 + 1;
		int height = halfHeight * 2 + 1;
		
		// Make room for the list of names
		height--;
		
		Map<Faction, Character> fList = new HashMap<Faction, Character>();
		int chrIdx = 0;
		
		// For each row
		for (int dz = 0; dz < height; dz++)
		{
			// Draw and add that row
			String row = "";
			for (int dx = 0; dx < width; dx++)
			{
				if(dx == halfWidth && dz == halfHeight)
				{
					row += ChatColor.AQUA+"+";
					continue;
				}
			
				PS herePs = topLeftPs.plusChunkCoords(dx, dz);
				Faction hereFaction = this.getFactionAt(herePs);
				if (hereFaction.isNone())
				{
					row += ChatColor.GRAY+"-";
				}
				else
				{
					if (!fList.containsKey(hereFaction))
						fList.put(hereFaction, Const.MAP_KEY_CHARS[chrIdx++]);
					char fchar = fList.get(hereFaction);
					row += hereFaction.getColorTo(observer) + "" + fchar;
				}
			}
			ret.add(row);
		}
		
		// Get the compass
		ArrayList<String> asciiCompass = AsciiCompass.getAsciiCompass(inDegrees, ChatColor.RED, Txt.parse("<a>"));

		// Add the compass
		ret.set(1, asciiCompass.get(0)+ret.get(1).substring(3*3));
		ret.set(2, asciiCompass.get(1)+ret.get(2).substring(3*3));
		ret.set(3, asciiCompass.get(2)+ret.get(3).substring(3*3));
			
		String fRow = "";
		for (Faction keyfaction : fList.keySet())
		{
			fRow += ""+keyfaction.getColorTo(observer) + fList.get(keyfaction) + ": " + keyfaction.getName() + " ";
		}
		fRow = fRow.trim();
		ret.add(fRow);
		
		return ret;
	}
	
}
