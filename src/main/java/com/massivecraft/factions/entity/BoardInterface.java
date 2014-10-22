package com.massivecraft.factions.entity;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.massivecore.ps.PS;

public interface BoardInterface
{
	// GET
	public TerritoryAccess getTerritoryAccessAt(PS ps);	
	public Faction getFactionAt(PS ps);
	
	// SET
	public void setTerritoryAccessAt(PS ps, TerritoryAccess territoryAccess);
	public void setFactionAt(PS ps, Faction faction);
	
	// REMOVE
	public void removeAt(PS ps);
	public void removeAll(Faction faction);
	public void clean();

	// CHUNKS
	public Set<PS> getChunks(Faction faction);
	public Set<PS> getChunks(String factionId);
	public Map<Faction, Set<PS>> getFactionToChunks();
	
	// COUNT
	public int getCount(Faction faction);
	public int getCount(String factionId);
	public Map<Faction, Integer> getFactionToCount();
	
	// NEARBY DETECTION
	public boolean isBorderPs(PS ps);
	public boolean isAnyBorderPs(Set<PS> pss);
	public boolean isConnectedPs(PS ps, Faction faction);
	public boolean isAnyConnectedPs(Set<PS> pss, Faction faction);
	
	// MAP
	// TODO: Could the degrees be embedded in centerPs yaw instead?
	public ArrayList<String> getMap(RelationParticipator observer, PS centerPs, double inDegrees, int width, int height);
	
}
