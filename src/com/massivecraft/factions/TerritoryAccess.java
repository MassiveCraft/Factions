package com.massivecraft.factions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.UPlayerColl;
import com.massivecraft.factions.entity.UPlayerColls;

public class TerritoryAccess
{
	// -------------------------------------------- //
	// FIELDS: RAW
	// -------------------------------------------- //
	
	// no default value, can't be null
	private final String hostFactionId;
	public String getHostFactionId() { return this.hostFactionId; }
	
	// default is true
	private final boolean hostFactionAllowed;
	public boolean isHostFactionAllowed() { return this.hostFactionAllowed; }
	
	// default is empty
	private final Set<String> factionIds;
	public Set<String> getFactionIds() { return this.factionIds; }
	
	// default is empty
	private final Set<String> playerIds;
	public Set<String> getPlayerIds() { return this.playerIds; }
	
	// -------------------------------------------- //
	// FIELDS: DELTA
	// -------------------------------------------- //
	
	// The simple ones
	public TerritoryAccess withHostFactionId(String hostFactionId) { return valueOf(hostFactionId, hostFactionAllowed, factionIds, playerIds); }
	public TerritoryAccess withHostFactionAllowed(Boolean hostFactionAllowed) { return valueOf(hostFactionId, hostFactionAllowed, factionIds, playerIds); }
	public TerritoryAccess withFactionIds(Collection<String> factionIds) { return valueOf(hostFactionId, hostFactionAllowed, factionIds, playerIds); }
	public TerritoryAccess withPlayerIds(Collection<String> playerIds) { return valueOf(hostFactionId, hostFactionAllowed, factionIds, playerIds); }
	
	// The intermediate ones
	public TerritoryAccess withFactionId(String factionId, boolean with)
	{
		if (this.getHostFactionId().equals(factionId))
		{
			return valueOf(hostFactionId, with, factionIds, playerIds);
		}
		
		Set<String> factionIds = new HashSet<String>(this.getFactionIds());
		if (with)
		{
			factionIds.add(factionId);
		}
		else
		{
			factionIds.remove(factionId);
		}
		return valueOf(hostFactionId, hostFactionAllowed, factionIds, playerIds);
	}
	
	public TerritoryAccess withPlayerId(String playerId, boolean with)
	{
		playerId = playerId.toLowerCase();
		Set<String> playerIds = new HashSet<String>(this.getPlayerIds());
		if (with)
		{
			playerIds.add(playerId);
		}
		else
		{
			playerIds.remove(playerId);
		}
		return valueOf(hostFactionId, hostFactionAllowed, factionIds, playerIds);
	}
	
	// The complex ones
	public TerritoryAccess toggleFactionId(String factionId)
	{
		return this.withFactionId(factionId, !this.isFactionIdGranted(factionId));
	}
	
	public TerritoryAccess togglePlayerId(String playerId)
	{
		return this.withPlayerId(playerId, !this.isPlayerIdGranted(playerId));
	}
	
	// -------------------------------------------- //
	// FIELDS: UNIVERSED
	// -------------------------------------------- //
	
	public Faction getHostFaction(Object universe)
	{
		return FactionColls.get().get(universe).get(this.getHostFactionId());
	}
	
	public LinkedHashSet<UPlayer> getGrantedUPlayers(Object universe)
	{
		LinkedHashSet<UPlayer> ret = new LinkedHashSet<UPlayer>();
		UPlayerColl coll = UPlayerColls.get().get(universe);
		for (String playerId : this.getPlayerIds())
		{
			ret.add(coll.get(playerId));
		}
		return ret;
	}
	
	public LinkedHashSet<Faction> getGrantedFactions(Object universe)
	{
		LinkedHashSet<Faction> ret = new LinkedHashSet<Faction>();
		FactionColl coll = FactionColls.get().get(universe);
		for (String factionId : this.getFactionIds())
		{
			ret.add(coll.get(factionId));
		}
		return ret;
	}

	// -------------------------------------------- //
	// PRIVATE CONSTRUCTOR
	// -------------------------------------------- //
	
	private TerritoryAccess(String hostFactionId, Boolean hostFactionAllowed, Collection<String> factionIds, Collection<String> playerIds)
	{
		if (hostFactionId == null) throw new IllegalArgumentException("hostFactionId was null");
		this.hostFactionId = hostFactionId;
		
		Set<String> factionIdsInner = new TreeSet<String>();
		if (factionIds != null)
		{
			factionIdsInner.addAll(factionIds);
			if (factionIdsInner.remove(hostFactionId))
			{
				hostFactionAllowed = true;
			}
		}
		this.factionIds = Collections.unmodifiableSet(factionIdsInner);
		
		Set<String> playerIdsInner = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		if (playerIds != null)
		{
			for (String playerId : playerIds)
			{
				playerIdsInner.add(playerId.toLowerCase());
			}
		}
		this.playerIds = Collections.unmodifiableSet(playerIdsInner);
		
		this.hostFactionAllowed = (hostFactionAllowed == null || hostFactionAllowed);
	}
	
	// -------------------------------------------- //
	// FACTORY: VALUE OF
	// -------------------------------------------- //
	
	public static TerritoryAccess valueOf(String hostFactionId, Boolean hostFactionAllowed, Collection<String> factionIds, Collection<String> playerIds)
	{
		return new TerritoryAccess(hostFactionId, hostFactionAllowed, factionIds, playerIds);
	}
	
	public static TerritoryAccess valueOf(String hostFactionId)
	{
		return valueOf(hostFactionId, null, null, null);
	}
	
	// -------------------------------------------- //
	// INSTANCE METHODS
	// -------------------------------------------- //
	
	public boolean isFactionIdGranted(String factionId)
	{
		if (this.getHostFactionId().equals(factionId))
		{
			return this.isHostFactionAllowed();
		}
		return this.getFactionIds().contains(factionId);
	}
	
	// Note that the player can have access without being specifically granted.
	// The player could for example be a member of a granted faction. 
	public boolean isPlayerIdGranted(String playerId)
	{
		return this.getPlayerIds().contains(playerId);
	}
	
	// A "default" TerritoryAccess could be serialized as a simple string only.
	// The host faction is still allowed (default) and no faction or player has been granted explicit access (default).
	public boolean isDefault()
	{
		return this.isHostFactionAllowed() && this.getFactionIds().isEmpty() && this.getPlayerIds().isEmpty(); 
	}

	// -------------------------------------------- //
	// HAS CHECK
	// -------------------------------------------- //
	
	// true means elevated access
	// false means decreased access
	// null means standard access
	public Boolean hasTerritoryAccess(UPlayer uplayer)
	{
		if (this.getPlayerIds().contains(uplayer.getId())) return true;
		
		String factionId = uplayer.getFactionId();
		if (this.getFactionIds().contains(factionId)) return true;
		
		if (this.getHostFactionId().equals(factionId) && !this.isHostFactionAllowed()) return false;
		
		return null;
	}
	
}