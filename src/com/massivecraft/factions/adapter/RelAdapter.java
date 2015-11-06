package com.massivecraft.factions.adapter;

import java.lang.reflect.Type;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.type.TypeRel;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.xlib.gson.JsonDeserializationContext;
import com.massivecraft.massivecore.xlib.gson.JsonDeserializer;
import com.massivecraft.massivecore.xlib.gson.JsonElement;
import com.massivecraft.massivecore.xlib.gson.JsonParseException;

public class RelAdapter implements JsonDeserializer<Rel>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static RelAdapter i = new RelAdapter();
	public static RelAdapter get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Rel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		try
		{
			return TypeRel.get().read(json.getAsString());
		}
		catch (MassiveException e)
		{
			return null;
		}
	}
	
}
