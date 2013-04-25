package com.massivecraft.factions;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;

public class TerritoryAccess
{
	// -------------------------------------------- //
	// FIELDS: RAW
	// -------------------------------------------- //
	
	private String hostFactionId;
	public String getHostFactionId() { return this.hostFactionId; }
	public void setHostFactionId(String hostFactionId) { this.hostFactionId = hostFactionId; }
	
	private boolean hostFactionAllowed = true;
	public boolean isHostFactionAllowed() { return this.hostFactionAllowed; }
	public void setHostFactionAllowed(boolean hostFactionAllowed) { this.hostFactionAllowed = hostFactionAllowed; }
	
	private Set<String> factionIds = new LinkedHashSet<String>();
	public Set<String> getFactionIds() { return this.factionIds; }
	
	private Set<String> fplayerIds = new LinkedHashSet<String>();
	public Set<String> getFPlayerIds() { return this.fplayerIds; }

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public TerritoryAccess(String hostFactionId)
	{
		this.hostFactionId = hostFactionId;
	}

	public TerritoryAccess()
	{
		
	}
	
	// -------------------------------------------- //
	// FIELDS: UTILS
	// -------------------------------------------- //

	public void addFaction(String factionId) { this.getFactionIds().add(factionId); }
	public void addFaction(Faction faction) { this.addFaction(faction.getId()); }
	public void removeFaction(String factionId) { this.getFactionIds().remove(factionId); }
	public void removeFaction(Faction faction) { this.removeFaction(faction.getId()); }
	
	// return true if faction was added, false if it was removed
	public boolean toggleFaction(String factionId)
	{
		// if the host faction, special handling
		if (this.doesHostFactionMatch(factionId))
		{
			this.hostFactionAllowed ^= true;
			return this.hostFactionAllowed;
		}

		if (this.getFactionIds().contains(factionId))
		{
			this.removeFaction(factionId);
			return false;
		}
		this.addFaction(factionId);
		return true;
	}
	public boolean toggleFaction(Faction faction)
	{
		return this.toggleFaction(faction.getId());
	}
	
	
	public void addFPlayer(String fplayerID) { this.getFPlayerIds().add(fplayerID); }
	public void addFPlayer(UPlayer fplayer) { this.addFPlayer(fplayer.getId()); }
	public void removeFPlayer(String fplayerID) { this.getFPlayerIds().remove(fplayerID); }
	public void removeFPlayer(UPlayer fplayer) { this.removeFPlayer(fplayer.getId()); }
	
	public boolean toggleFPlayer(String fplayerID)
	{
		if (this.getFPlayerIds().contains(fplayerID))
		{
			this.removeFPlayer(fplayerID);
			return false;
		}
		this.addFPlayer(fplayerID);
		return true;
	}
	public boolean toggleFPlayer(UPlayer fplayer)
	{
		return this.toggleFPlayer(fplayer.getId());
	}
	
	
	public boolean doesHostFactionMatch(Object testSubject)
	{
		if (testSubject instanceof String)
			return hostFactionId.equals((String)testSubject);
		else if (testSubject instanceof CommandSender)
			return hostFactionId.equals(UPlayer.get(testSubject).getFactionId());
		else if (testSubject instanceof UPlayer)
			return hostFactionId.equals(((UPlayer)testSubject).getFactionId());
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
		return this.hostFactionAllowed && this.factionIds.isEmpty() && this.fplayerIds.isEmpty();
	}
	
	public void setDefault(String factionId)
	{
		this.hostFactionId = factionId;
		this.hostFactionAllowed = true;
		this.factionIds.clear();
		this.fplayerIds.clear();
	}

	public String factionList(Faction universeExtractable)
	{
		StringBuilder list = new StringBuilder();
		for (String factionID : factionIds)
		{
			if (list.length() > 0)
				list.append(", ");
			list.append(FactionColls.get().get(universeExtractable).get(factionID).getName());
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
			return fPlayerHasAccess(UPlayer.get(testSubject));
		else if (testSubject instanceof UPlayer)
			return fPlayerHasAccess((UPlayer)testSubject);
		else if (testSubject instanceof Faction)
			return factionHasAccess((Faction)testSubject);
		return false;
	}
	public boolean fPlayerHasAccess(UPlayer fplayer)
	{
		if (factionHasAccess(fplayer.getFactionId())) return true;
		return fplayerIds.contains(fplayer.getId());
	}
	public boolean factionHasAccess(Faction faction)
	{
		return factionHasAccess(faction.getId());
	}
	public boolean factionHasAccess(String factionId)
	{
		return factionIds.contains(factionId);
	}

	// this should normally only be checked after running subjectHasAccess() or fPlayerHasAccess() above to see if they have access explicitly granted
	public boolean subjectAccessIsRestricted(Object testSubject)
	{
		Faction hostFaction = FactionColls.get().get(testSubject).get(this.getHostFactionId());
		return ( ! this.isHostFactionAllowed() && this.doesHostFactionMatch(testSubject) && ! FPerm.ACCESS.has(testSubject, hostFaction, false));
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