package com.massivecraft.factions.adapters;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.massivecraft.factions.struct.FFlag;

public class FFlagTypeAdapter implements JsonDeserializer<FFlag>
{
	@Override
	public FFlag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		return FFlag.parse(json.getAsString());
	}
}
