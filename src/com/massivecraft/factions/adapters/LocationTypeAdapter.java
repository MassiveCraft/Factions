package com.massivecraft.factions.adapters;

import java.lang.reflect.Type;
import java.util.logging.Level;

import com.massivecraft.factions.P;
import com.massivecraft.factions.util.LazyLocation;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


public class LocationTypeAdapter implements JsonDeserializer<LazyLocation>, JsonSerializer<LazyLocation>
{
	private static final String WORLD = "world";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String Z = "z";
	private static final String YAW = "yaw";
	private static final String PITCH = "pitch";
	
	@Override
	public LazyLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		try
		{
			JsonObject obj = json.getAsJsonObject();

			String worldName = obj.get(WORLD).getAsString();
			double x = obj.get(X).getAsDouble();
			double y = obj.get(Y).getAsDouble();
			double z = obj.get(Z).getAsDouble();
			float yaw = obj.get(YAW).getAsFloat();
			float pitch = obj.get(PITCH).getAsFloat();

			return new LazyLocation(worldName, x, y, z, yaw, pitch);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			P.p.log(Level.WARNING, "Error encountered while deserializing a LazyLocation.");
			return null;
		}
	}

	@Override
	public JsonElement serialize(LazyLocation src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();

		try
		{
			obj.addProperty(WORLD, src.getWorldName());
			obj.addProperty(X, src.getX());
			obj.addProperty(Y, src.getY());
			obj.addProperty(Z, src.getZ());
			obj.addProperty(YAW, src.getYaw());
			obj.addProperty(PITCH, src.getPitch());

			return obj;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			P.p.log(Level.WARNING, "Error encountered while serializing a LazyLocation.");
			return obj;
		}
	}
}
