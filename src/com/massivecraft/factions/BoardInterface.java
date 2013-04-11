package com.massivecraft.factions;

import java.util.ArrayList;

import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.mcore.ps.PS;

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
	
	// COUNT
	public int getCount(Faction faction);
	
	// NEARBY DETECTION
	public boolean isBorderPs(PS ps);
	public boolean isConnectedPs(PS ps, Faction faction);
	
	// MAP
	// TODO: Could the degrees be embedded in centerPs yaw instead?
	public ArrayList<String> getMap(RelationParticipator observer, PS centerPs, double inDegrees);
	
}
