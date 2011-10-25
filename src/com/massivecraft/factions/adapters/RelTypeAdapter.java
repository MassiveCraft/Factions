package com.massivecraft.factions.adapters;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.massivecraft.factions.struct.Rel;

public class RelTypeAdapter implements JsonDeserializer<Rel>
{
	@Override
	public Rel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		return Rel.parse(json.getAsString());
	}
}
