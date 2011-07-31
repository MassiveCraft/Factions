package com.massivecraft.factions.util;

import java.lang.reflect.Type;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.World;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.massivecraft.factions.Factions;


public class MyLocationTypeAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {
	private static final String WORLD = "world";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String Z = "z";
	private static final String YAW = "yaw";
	private static final String PITCH = "pitch";
	
	@Override
	public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		try {
			JsonObject obj = json.getAsJsonObject();

			String worldname = obj.get(WORLD).getAsString();
			World world = Factions.instance.getServer().getWorld(worldname);
			if (world == null) {
				Factions.log(Level.WARNING, "Stored location's world \"" + worldname + "\" not found on server; dropping the location.");
				return null;
			}

			double x = obj.get(X).getAsDouble();
			double y = obj.get(Y).getAsDouble();
			double z = obj.get(Z).getAsDouble();
			float yaw = obj.get(YAW).getAsFloat();
			float pitch = obj.get(PITCH).getAsFloat();

			return new Location(world, x, y, z, yaw, pitch);

		} catch (Exception ex) {
			ex.printStackTrace();
			Factions.log(Level.WARNING, "Error encountered while deserializing a location.");
			return null;
		}
	}

	@Override
	public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();

		try {
			if (src.getWorld() == null)
			{
				Factions.log(Level.WARNING, "Passed location's world was not found on the server. Dropping the location.");
				return obj;
			}

			obj.addProperty(WORLD, src.getWorld().getName());
			obj.addProperty(X, src.getX());
			obj.addProperty(Y, src.getY());
			obj.addProperty(Z, src.getZ());
			obj.addProperty(YAW, src.getYaw());
			obj.addProperty(PITCH, src.getPitch());

			return obj;

		} catch (Exception ex) {
			ex.printStackTrace();
			Factions.log(Level.WARNING, "Error encountered while serializing a location.");
			return obj;
		}
	}
}
