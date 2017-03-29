package com.massivecraft.factions.entity;

import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.massivecore.ps.PS;

import java.util.Map;
import java.util.Set;

public interface BoardInterface
{
	// GET
	TerritoryAccess getTerritoryAccessAt(PS ps);
	Faction getFactionAt(PS ps);
	
	// SET
	void setTerritoryAccessAt(PS ps, TerritoryAccess territoryAccess);
	void setFactionAt(PS ps, Faction faction);
	
	// REMOVE
	void removeAt(PS ps);
	void removeAll(Faction faction);

	// CHUNKS
	Set<PS> getChunks(Faction faction);
	Set<PS> getChunks(String factionId);
	Map<Faction, Set<PS>> getFactionToChunks();
	
	// COUNT
	int getCount(Faction faction);
	int getCount(String factionId);
	Map<Faction, Integer> getFactionToCount();
	
	// CLAIMED
	boolean hasClaimed(Faction faction);
	boolean hasClaimed(String factionId);
	
	// NEARBY DETECTION
	boolean isBorderPs(PS ps);
	boolean isAnyBorderPs(Set<PS> pss);
	boolean isConnectedPs(PS ps, Faction faction);
	boolean isAnyConnectedPs(Set<PS> pss, Faction faction);
	
}
