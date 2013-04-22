package com.massivecraft.factions.adapter;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

import com.massivecraft.mcore.ps.PS;
import com.massivecraft.mcore.xlib.gson.JsonDeserializationContext;
import com.massivecraft.mcore.xlib.gson.JsonDeserializer;
import com.massivecraft.mcore.xlib.gson.JsonElement;
import com.massivecraft.mcore.xlib.gson.JsonObject;
import com.massivecraft.mcore.xlib.gson.JsonParseException;
import com.massivecraft.mcore.xlib.gson.JsonSerializationContext;
import com.massivecraft.mcore.xlib.gson.JsonSerializer;

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