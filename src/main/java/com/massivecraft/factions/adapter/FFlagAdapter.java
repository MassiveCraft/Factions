package com.massivecraft.factions.adapter;

import java.lang.reflect.Type;

import com.massivecraft.factions.FFlag;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class FFlagAdapter implements JsonDeserializer<FFlag>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FFlagAdapter i = new FFlagAdapter();
	public static FFlagAdapter get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public FFlag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		return FFlag.parse(json.getAsString());
	}
}
