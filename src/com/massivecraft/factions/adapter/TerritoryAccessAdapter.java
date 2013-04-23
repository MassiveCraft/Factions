package com.massivecraft.factions.adapter;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.logging.Level;

import com.massivecraft.mcore.xlib.gson.JsonArray;
import com.massivecraft.mcore.xlib.gson.JsonDeserializationContext;
import com.massivecraft.mcore.xlib.gson.JsonDeserializer;
import com.massivecraft.mcore.xlib.gson.JsonElement;
import com.massivecraft.mcore.xlib.gson.JsonObject;
import com.massivecraft.mcore.xlib.gson.JsonParseException;
import com.massivecraft.mcore.xlib.gson.JsonPrimitive;
import com.massivecraft.mcore.xlib.gson.JsonSerializationContext;
import com.massivecraft.mcore.xlib.gson.JsonSerializer;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.TerritoryAccess;

public class TerritoryAccessAdapter implements JsonDeserializer<TerritoryAccess>, JsonSerializer<TerritoryAccess>
{
	//----------------------------------------------//
	// CONSTANTS
	//----------------------------------------------//

	public static final String ID = "ID";
	public static final String OPEN = "open";
	public static final String FACTIONS = "factions";
	public static final String FPLAYERS = "fplayers";

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static TerritoryAccessAdapter i = new TerritoryAccessAdapter();
	public static TerritoryAccessAdapter get() { return i; }

	//----------------------------------------------//
	// OVERRIDE
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

			String factionID = obj.get(ID).getAsString();
			boolean hostAllowed = obj.get(OPEN).getAsBoolean();
			JsonArray factions = obj.getAsJsonArray(FACTIONS);
			JsonArray fplayers = obj.getAsJsonArray(FPLAYERS);

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
			Factions.get().log(Level.WARNING, "Error encountered while deserializing TerritoryAccess data.");
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
				if (src.getHostFactionId().equals(Const.FACTIONID_NONE))
					return null;

				return new JsonPrimitive(src.getHostFactionId());
			}

			// otherwise, store all data
			JsonObject obj = new JsonObject();

			JsonArray factions = new JsonArray();
			JsonArray fplayers = new JsonArray();

			Iterator<String> iter = src.getFactionIds().iterator();
			while (iter.hasNext())
			{
				factions.add(new JsonPrimitive(iter.next()));
			}

			iter = src.getFPlayerIds().iterator();
			while (iter.hasNext())
			{
				fplayers.add(new JsonPrimitive(iter.next()));
			}

			obj.addProperty(ID, src.getHostFactionId());
			obj.addProperty(OPEN, src.isHostFactionAllowed());
			obj.add(FACTIONS, factions);
			obj.add(FPLAYERS, fplayers);

			return obj;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Factions.get().log(Level.WARNING, "Error encountered while serializing TerritoryAccess data.");
			return null;
		}
	}

}
