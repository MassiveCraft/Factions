package com.massivecraft.factions.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.massivecraft.factions.struct.Rel;

/**
 * This is a legacy solution. Since Relation and Role enums have ben merged AND the rename ADMIN -> LEADER, MODERATOR -> OFFICER
 */
public class MyRelTypeAdapter implements JsonDeserializer<Rel>
{
	@Override
	public Rel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		return Rel.parse(json.getAsString());
	}
}
