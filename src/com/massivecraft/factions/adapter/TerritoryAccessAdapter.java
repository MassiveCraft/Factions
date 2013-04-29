package com.massivecraft.factions.adapter;

import java.lang.reflect.Type;
import java.util.Set;

import com.massivecraft.mcore.xlib.gson.JsonDeserializationContext;
import com.massivecraft.mcore.xlib.gson.JsonDeserializer;
import com.massivecraft.mcore.xlib.gson.JsonElement;
import com.massivecraft.mcore.xlib.gson.JsonObject;
import com.massivecraft.mcore.xlib.gson.JsonParseException;
import com.massivecraft.mcore.xlib.gson.JsonPrimitive;
import com.massivecraft.mcore.xlib.gson.JsonSerializationContext;
import com.massivecraft.mcore.xlib.gson.JsonSerializer;
import com.massivecraft.mcore.xlib.gson.reflect.TypeToken;

import com.massivecraft.factions.TerritoryAccess;

public class TerritoryAccessAdapter implements JsonDeserializer<TerritoryAccess>, JsonSerializer<TerritoryAccess>
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //

	public static final String HOST_FACTION_ID = "hostFactionId";
	public static final String HOST_FACTION_ALLOWED = "hostFactionAllowed";
	public static final String FACTION_IDS = "factionIds";
	public static final String PLAYER_IDS = "playerIds";
	
	public static final Type SET_OF_STRING_TYPE = new TypeToken<Set<String>>(){}.getType();
			
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
		// isDefault <=> simple hostFactionId string
		if (json.isJsonPrimitive())
		{
			String hostFactionId = json.getAsString();
			return TerritoryAccess.valueOf(hostFactionId);
		}

		// Otherwise object
		JsonObject obj = json.getAsJsonObject();

		// Prepare variables
		String hostFactionId = null;
		Boolean hostFactionAllowed = null;
		Set<String> factionIds = null;
		Set<String> playerIds = null;
		
		// Read variables (test old values first)
		JsonElement element = null;
		
		element = obj.get("ID");
		if (element == null) element = obj.get(HOST_FACTION_ID);
		hostFactionId = element.getAsString();
		
		element = obj.get("open");
		if (element == null) element = obj.get(HOST_FACTION_ALLOWED);
		if (element != null) hostFactionAllowed = element.getAsBoolean();
		
		element = obj.get("factions");
		if (element == null) element = obj.get(FACTION_IDS);
		if (element != null) factionIds = context.deserialize(element, SET_OF_STRING_TYPE);
		
		element = obj.get("fplayers");
		if (element == null) element = obj.get(PLAYER_IDS);
		if (element != null) playerIds = context.deserialize(element, SET_OF_STRING_TYPE);
		
		return TerritoryAccess.valueOf(hostFactionId, hostFactionAllowed, factionIds, playerIds);
	}

	@Override
	public JsonElement serialize(TerritoryAccess src, Type typeOfSrc, JsonSerializationContext context)
	{
		if (src == null) return null;

		// isDefault <=> simple hostFactionId string
		if (src.isDefault())
		{
			return new JsonPrimitive(src.getHostFactionId());
		}

		// Otherwise object
		JsonObject obj = new JsonObject();
		
		obj.addProperty(HOST_FACTION_ID, src.getHostFactionId());
		
		if (!src.isHostFactionAllowed())
		{
			obj.addProperty(HOST_FACTION_ALLOWED, src.isHostFactionAllowed());
		}
		
		if (!src.getFactionIds().isEmpty())
		{
			obj.add(FACTION_IDS, context.serialize(src.getFactionIds(), SET_OF_STRING_TYPE));
		}
		
		if (!src.getPlayerIds().isEmpty())
		{
			obj.add(PLAYER_IDS, context.serialize(src.getPlayerIds(), SET_OF_STRING_TYPE));
		}

		return obj;
	}
	
}