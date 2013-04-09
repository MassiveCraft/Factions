package com.massivecraft.factions;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public class TerritoryAccess
{
	// -------------------------------------------- //
	// FIELDS: RAW
	// -------------------------------------------- //
	
	private String hostFactionId;
	public String getHostFactionId()
	{
		return this.hostFactionId;
	}
	public Faction getHostFaction() { return FactionColl.i.get(this.hostFactionId); }
	
	public void setHostFactionId(String factionId)
	{
		// TODO: Why all these here???
		this.hostFactionId = factionId;
		this.hostFactionAllowed = true;
		this.factionIds.clear();
		this.fplayerIds.clear();
	}
	
	private boolean hostFactionAllowed = true;
	public boolean isHostFactionAllowed() { return this.hostFactionAllowed; }
	public void setHostFactionAllowed(boolean allowed) { this.hostFactionAllowed = allowed; }
	
	private Set<String> factionIds = new LinkedHashSet<String>();
	public Set<String> getFactionIds() { return this.factionIds; }
	
	private Set<String> fplayerIds = new LinkedHashSet<String>();
	public Set<String> getFPlayerIds() { return this.fplayerIds; }

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public TerritoryAccess(String factionID)
	{
		hostFactionId = factionID;
	}

	public TerritoryAccess()
	{
		
	}
	
	// -------------------------------------------- //
	// FIELDS: UTILS
	// -------------------------------------------- //

	public void addFaction(String factionId) { this.getFactionIds().add(factionId); }
	public void addFaction(Faction faction) { this.addFaction(faction.getId()); }
	public void removeFaction(String factionID) { this.getFactionIds().remove(factionID); }
	public void removeFaction(Faction faction) { this.removeFaction(faction.getId()); }
	
	// return true if faction was added, false if it was removed
	public boolean toggleFaction(String factionID)
	{
		// if the host faction, special handling
		if (doesHostFactionMatch(factionID))
		{
			this.hostFactionAllowed ^= true;
			return this.hostFactionAllowed;
		}

		if (this.getFactionIds().contains(factionID))
		{
			removeFaction(factionID);
			return false;
		}
		this.addFaction(factionID);
		return true;
	}
	public boolean toggleFaction(Faction faction)
	{
		return toggleFaction(faction.getId());
	}
	
	
	public void addFPlayer(String fplayerID) { this.getFPlayerIds().add(fplayerID); }
	public void addFPlayer(FPlayer fplayer) { this.addFPlayer(fplayer.getId()); }
	public void removeFPlayer(String fplayerID) { this.getFPlayerIds().remove(fplayerID); }
	public void removeFPlayer(FPlayer fplayer) { this.removeFPlayer(fplayer.getId()); }
	
	public boolean toggleFPlayer(String fplayerID)
	{
		if (this.getFPlayerIds().contains(fplayerID))
		{
			removeFPlayer(fplayerID);
			return false;
		}
		addFPlayer(fplayerID);
		return true;
	}
	public boolean toggleFPlayer(FPlayer fplayer)
	{
		return toggleFPlayer(fplayer.getId());
	}
	
	
	
	public boolean doesHostFactionMatch(Object testSubject)
	{
		if (testSubject instanceof String)
			return hostFactionId.equals((String)testSubject);
		else if (testSubject instanceof Player)
			return hostFactionId.equals(FPlayerColl.i.get((Player)testSubject).getFactionId());
		else if (testSubject instanceof FPlayer)
			return hostFactionId.equals(((FPlayer)testSubject).getFactionId());
		else if (testSubject instanceof Faction)
			return hostFactionId.equals(((Faction)testSubject).getId());
		return false;
	}
	
	// -------------------------------------------- //
	// UTILS
	// -------------------------------------------- //
	
	// considered "default" if host faction is still allowed and nobody has been granted access
	public boolean isDefault()
	{
		return this.hostFactionAllowed && factionIds.isEmpty() && fplayerIds.isEmpty();
	}
	

	public String factionList()
	{
		StringBuilder list = new StringBuilder();
		for (String factionID : factionIds)
		{
			if (list.length() > 0)
				list.append(", ");
			list.append(FactionColl.i.get(factionID).getTag());
		}
		return list.toString();
	}

	public String fplayerList()
	{
		StringBuilder list = new StringBuilder();
		for (String fplayerID : fplayerIds)
		{
			if (list.length() > 0)
				list.append(", ");
			list.append(fplayerID);
		}
		return list.toString();
	}

	// these return false if not granted explicit access, or true if granted explicit access (in FPlayer or Faction lists)
	// they do not take into account hostFactionAllowed, which will need to be checked separately (as to not override FPerms which are denied for faction members and such)
	public boolean subjectHasAccess(Object testSubject)
	{
		if (testSubject instanceof Player)
			return fPlayerHasAccess(FPlayerColl.i.get((Player)testSubject));
		else if (testSubject instanceof FPlayer)
			return fPlayerHasAccess((FPlayer)testSubject);
		else if (testSubject instanceof Faction)
			return factionHasAccess((Faction)testSubject);
		return false;
	}
	public boolean fPlayerHasAccess(FPlayer fplayer)
	{
		if (factionHasAccess(fplayer.getFactionId())) return true;
		return fplayerIds.contains(fplayer.getId());
	}
	public boolean factionHasAccess(Faction faction)
	{
		return factionHasAccess(faction.getId());
	}
	public boolean factionHasAccess(String factionID)
	{
		return factionIds.contains(factionID);
	}

	// this should normally only be checked after running subjectHasAccess() or fPlayerHasAccess() above to see if they have access explicitly granted
	public boolean subjectAccessIsRestricted(Object testSubject)
	{
		return ( ! this.isHostFactionAllowed() && this.doesHostFactionMatch(testSubject) && ! FPerm.ACCESS.has(testSubject, this.getHostFaction()));
	}

	//----------------------------------------------//
	// COMPARISON
	//----------------------------------------------//

	@Override
	public int hashCode()
	{
		return this.hostFactionId.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) return true;
		
		if (!(obj instanceof TerritoryAccess)) return false;

		TerritoryAccess that = (TerritoryAccess) obj;
		return this.hostFactionId.equals(that.hostFactionId) && this.hostFactionAllowed == that.hostFactionAllowed && this.factionIds == that.factionIds && this.fplayerIds == that.fplayerIds;
	}
}