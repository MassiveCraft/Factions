package com.massivecraft.factions.adapter;

import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.xlib.gson.JsonDeserializationContext;
import com.massivecraft.massivecore.xlib.gson.JsonDeserializer;
import com.massivecraft.massivecore.xlib.gson.JsonElement;
import com.massivecraft.massivecore.xlib.gson.JsonObject;
import com.massivecraft.massivecore.xlib.gson.JsonParseException;
import com.massivecraft.massivecore.xlib.gson.JsonSerializationContext;
import com.massivecraft.massivecore.xlib.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

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
		Map<PS, TerritoryAccess> ret = new ConcurrentSkipListMap<>();
		
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
