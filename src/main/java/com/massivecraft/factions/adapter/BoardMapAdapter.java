package com.massivecraft.factions.adapter;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

import com.massivecraft.massivecore.ps.PS;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.massivecraft.factions.TerritoryAccess;

public class BoardMapAdapter implements JsonDeserializer<Map<PS, TerritoryAccess>>, JsonSerializer<Map<PS, TerritoryAccess>>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static BoardMapAdapter i = new BoardMapAdapter();
	public static BoardMapAdapter get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Map<PS, TerritoryAccess> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		Map<PS, TerritoryAccess> ret = new ConcurrentSkipListMap<PS, TerritoryAccess>();
		
		JsonObject jsonObject = json.getAsJsonObject();
		
		for (Entry<String, JsonElement> entry : jsonObject.entrySet())
		{
			String[] ChunkCoordParts = entry.getKey().split("[,\\s]+");
			int chunkX = Integer.parseInt(ChunkCoordParts[0]);
			int chunkZ = Integer.parseInt(ChunkCoordParts[1]);
			PS chunk = PS.valueOf(chunkX, chunkZ);
			
			TerritoryAccess territoryAccess = context.deserialize(entry.getValue(), TerritoryAccess.class);
			
			ret.put(chunk, territoryAccess);
		}
		
		return ret;
	}

	@Override
	public JsonElement serialize(Map<PS, TerritoryAccess> src, Type typeOfSrc, JsonSerializationContext context)
	{
		JsonObject ret = new JsonObject();
		
		for (Entry<PS, TerritoryAccess> entry : src.entrySet())
		{
			PS ps = entry.getKey();
			TerritoryAccess territoryAccess = entry.getValue();
			
			ret.add(ps.getChunkX().toString() + "," + ps.getChunkZ().toString(), context.serialize(territoryAccess, TerritoryAccess.class));
		}
		
		return ret;
	}
	
}