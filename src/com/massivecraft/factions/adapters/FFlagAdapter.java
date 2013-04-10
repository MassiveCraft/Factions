package com.massivecraft.factions.adapters;

import java.lang.reflect.Type;

import com.massivecraft.factions.FFlag;
import com.massivecraft.mcore.xlib.gson.JsonDeserializationContext;
import com.massivecraft.mcore.xlib.gson.JsonDeserializer;
import com.massivecraft.mcore.xlib.gson.JsonElement;
import com.massivecraft.mcore.xlib.gson.JsonParseException;

public class FFlagAdapter implements JsonDeserializer<FFlag>
{
	@Override
	public FFlag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		return FFlag.parse(json.getAsString());
	}
}
