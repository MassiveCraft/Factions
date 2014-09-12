package com.massivecraft.factions.adapter;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Faction;

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
	
	public void rename(final JsonObject jsonObject, final String from, final String to)
	{
		JsonElement element = jsonObject.remove(from);
		if (element != null) jsonObject.add(to, element);
	}
	
}
