package com.massivecraft.factions.struct;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonArray;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonPrimitive;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;
import com.massivecraft.factions.FPlayers;


public class TerritoryAccess implements JsonDeserializer<TerritoryAccess>, JsonSerializer<TerritoryAccess>
{
	private String hostFactionID;
	private boolean hostFactionAllowed = true;
	private Set<String> factionIDs = new LinkedHashSet<String>();
	private Set<String> fplayerIDs = new LinkedHashSet<String>();


	public TerritoryAccess(String factionID)
	{
		hostFactionID = factionID;
	}

	public TerritoryAccess() {}


	public void setHostFactionID(String factionID)
	{
		hostFactionID = factionID;
		hostFactionAllowed = true;
		factionIDs.clear();
		fplayerIDs.clear();
	}
	public String getHostFactionID()
	{
		return hostFactionID;
	}
	public Faction getHostFaction()
	{
		return Factions.i.get(hostFactionID);
	}

	// considered "default" if host faction is still allowed and nobody has been granted access
	public boolean isDefault()
	{
		return this.hostFactionAllowed && factionIDs.isEmpty() && fplayerIDs.isEmpty();
	}

	public boolean isHostFactionAllowed()
	{
		return this.hostFactionAllowed;
	}
	public void setHostFactionAllowed(boolean allowed)
	{
		this.hostFactionAllowed = allowed;
	}

	public boolean doesHostFactionMatch(Object testSubject)
	{
		if (testSubject instanceof String)
			return hostFactionID.equals((String)testSubject);
		else if (testSubject instanceof Player)
			return hostFactionID.equals(FPlayers.i.get((Player)testSubject).getFactionId());
		else if (testSubject instanceof FPlayer)
			return hostFactionID.equals(((FPlayer)testSubject).getFactionId());
		else if (testSubject instanceof Faction)
			return hostFactionID.equals(((Faction)testSubject).getId());
		return false;
	}

	public void addFaction(String factionID)
	{
		factionIDs.add(factionID);
	}
	public void addFaction(Faction faction)
	{
		addFaction(faction.getId());
	}

	public void addFPlayer(String fplayerID)
	{
		fplayerIDs.add(fplayerID);
	}
	public void addFPlayer(FPlayer fplayer)
	{
		addFPlayer(fplayer.getId());
	}

	public void removeFaction(String factionID)
	{
		factionIDs.remove(factionID);
	}
	public void removeFaction(Faction faction)
	{
		removeFaction(faction.getId());
	}

	public void removeFPlayer(String fplayerID)
	{
		fplayerIDs.remove(fplayerID);
	}
	public void removeFPlayer(FPlayer fplayer)
	{
		removeFPlayer(fplayer.getId());
	}

	// return true if faction was added, false if it was removed
	public boolean toggleFaction(String factionID)
	{
		// if the host faction, special handling
		if (doesHostFactionMatch(factionID))
		{
			hostFactionAllowed ^= true;
			return hostFactionAllowed;
		}

		if (factionIDs.contains(factionID))
		{
			removeFaction(factionID);
			return false;
		}
		addFaction(factionID);
		return true;
	}
	public boolean toggleFaction(Faction faction)
	{
		return toggleFaction(faction.getId());
	}

	public boolean toggleFPlayer(String fplayerID)
	{
		if (fplayerIDs.contains(fplayerID))
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

	public String factionList()
	{
		StringBuilder list = new StringBuilder();
		for (String factionID : factionIDs)
		{
			if (list.length() > 0)
				list.append(", ");
			list.append(Factions.i.get(factionID).getTag());
		}
		return list.toString();
	}

	public String fplayerList()
	{
		StringBuilder list = new StringBuilder();
		for (String fplayerID : fplayerIDs)
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
			return fPlayerHasAccess(FPlayers.i.get((Player)testSubject));
		else if (testSubject instanceof FPlayer)
			return fPlayerHasAccess((FPlayer)testSubject);
		else if (testSubject instanceof Faction)
			return factionHasAccess((Faction)testSubject);
		return false;
	}
	public boolean fPlayerHasAccess(FPlayer fplayer)
	{
		if (factionHasAccess(fplayer.getFactionId())) return true;
		return fplayerIDs.contains(fplayer.getId());
	}
	public boolean factionHasAccess(Faction faction)
	{
		return factionHasAccess(faction.getId());
	}
	public boolean factionHasAccess(String factionID)
	{
		return factionIDs.contains(factionID);
	}

	// this should normally only be checked after running subjectHasAccess() or fPlayerHasAccess() above to see if they have access explicitly granted
	public boolean subjectAccessIsRestricted(Object testSubject)
	{
		return ( ! this.isHostFactionAllowed() && this.doesHostFactionMatch(testSubject) && ! FPerm.ACCESS.has(testSubject, this.getHostFaction()));
	}


	//----------------------------------------------//
	// JSON Serialize/Deserialize Type Adapters
	//----------------------------------------------//

	@Override
	public TerritoryAccess deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		try
		{
			// if stored as simple string, it's just the faction ID and default values are to be used
			if (json.isJsonPrimitive())
			{
				String factionID = json.getAsString();
				return new TerritoryAccess(factionID);
			}

			// otherwise, it's stored as an object and all data should be present
			JsonObject obj = json.getAsJsonObject();
			if (obj == null) return null;

			String factionID = obj.get("ID").getAsString();
			boolean hostAllowed = obj.get("open").getAsBoolean();
			JsonArray factions = obj.getAsJsonArray("factions");
			JsonArray fplayers = obj.getAsJsonArray("fplayers");

			TerritoryAccess access = new TerritoryAccess(factionID);
			access.setHostFactionAllowed(hostAllowed);

			Iterator<JsonElement> iter = factions.iterator();
			while (iter.hasNext())
			{
				access.addFaction(iter.next().getAsString());
			}

			iter = fplayers.iterator();
			while (iter.hasNext())
			{
				access.addFPlayer(iter.next().getAsString());
			}

			return access;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			P.p.log(Level.WARNING, "Error encountered while deserializing TerritoryAccess data.");
			return null;
		}
	}

	@Override
	public JsonElement serialize(TerritoryAccess src, Type typeOfSrc, JsonSerializationContext context)
	{
		try
		{
			if (src == null) return null;

			// if default values, store as simple string
			if (src.isDefault())
			{
				// if Wilderness (faction "0") and default access values, no need to store it
				if (src.getHostFactionID().equals("0"))
					return null;

				return new JsonPrimitive(src.getHostFactionID());
			}

			// otherwise, store all data
			JsonObject obj = new JsonObject();

			JsonArray factions = new JsonArray();
			JsonArray fplayers = new JsonArray();

			Iterator<String> iter = src.factionIDs.iterator();
			while (iter.hasNext())
			{
				factions.add(new JsonPrimitive(iter.next()));
			}

			iter = src.fplayerIDs.iterator();
			while (iter.hasNext())
			{
				fplayers.add(new JsonPrimitive(iter.next()));
			}

			obj.addProperty("ID", src.getHostFactionID());
			obj.addProperty("open", src.isHostFactionAllowed());
			obj.add("factions", factions);
			obj.add("fplayers", fplayers);

			return obj;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			P.p.log(Level.WARNING, "Error encountered while serializing TerritoryAccess data.");
			return null;
		}
	}


	//----------------------------------------------//
	// Comparison
	//----------------------------------------------//

	@Override
	public int hashCode()
	{
		return this.hostFactionID.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (!(obj instanceof TerritoryAccess))
			return false;

		TerritoryAccess that = (TerritoryAccess) obj;
		return this.hostFactionID.equals(that.hostFactionID) && this.hostFactionAllowed == that.hostFactionAllowed && this.factionIDs == that.factionIDs && this.fplayerIDs == that.fplayerIDs;
	}
}