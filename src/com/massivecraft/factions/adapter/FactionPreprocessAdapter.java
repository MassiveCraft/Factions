package com.massivecraft.factions.adapter;

import java.lang.reflect.Type;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.xlib.gson.JsonDeserializationContext;
import com.massivecraft.massivecore.xlib.gson.JsonDeserializer;
import com.massivecraft.massivecore.xlib.gson.JsonElement;
import com.massivecraft.massivecore.xlib.gson.JsonObject;
import com.massivecraft.massivecore.xlib.gson.JsonParseException;

public class FactionPreprocessAdapter implements JsonDeserializer<Faction>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FactionPreprocessAdapter i = new FactionPreprocessAdapter();
	public static FactionPreprocessAdapter get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Faction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		preprocess(json);
		return Factions.get().gsonWithoutPreprocessors.fromJson(json, typeOfT);
	}
	
	public void preprocess(JsonElement json)
	{
		JsonObject jsonObject = json.getAsJsonObject();
		
		// Renamed fields
		// 1.8.X --> 2.0.0
		rename(jsonObject, "tag", "name");
		rename(jsonObject, "invites", "invitedPlayerIds");
		rename(jsonObject, "relationWish", "relationWishes");
		rename(jsonObject, "flagOverrides", "flags");
		rename(jsonObject, "permOverrides", "perms");
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public static void rename(final JsonObject jsonObject, final String from, final String to)
	{
		JsonElement element = jsonObject.remove(from);
		if (element != null) jsonObject.add(to, element);
	}
	
}
