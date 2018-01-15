package com.massivecraft.factions.util;

import com.google.gson.*;
import com.massivecraft.factions.P;

import java.lang.reflect.Type;
import java.util.logging.Level;


public class MyLocationTypeAdapter implements JsonDeserializer<LazyLocation>, JsonSerializer<LazyLocation> {

    private static final String WORLD = "world";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";
    private static final String YAW = "yaw";
    private static final String PITCH = "pitch";

    @Override
    public LazyLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject obj = json.getAsJsonObject();

            String worldName = obj.get(WORLD).getAsString();
            double x = obj.get(X).getAsDouble();
            double y = obj.get(Y).getAsDouble();
            double z = obj.get(Z).getAsDouble();
            float yaw = obj.get(YAW).getAsFloat();
            float pitch = obj.get(PITCH).getAsFloat();

            return new LazyLocation(worldName, x, y, z, yaw, pitch);

        } catch (Exception ex) {
            ex.printStackTrace();
            P.p.log(Level.WARNING, "Error encountered while deserializing a LazyLocation.");
            return null;
        }
    }

    @Override
    public JsonElement serialize(LazyLocation src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();

        try {
            obj.addProperty(WORLD, src.getWorldName());
            obj.addProperty(X, src.getX());
            obj.addProperty(Y, src.getY());
            obj.addProperty(Z, src.getZ());
            obj.addProperty(YAW, src.getYaw());
            obj.addProperty(PITCH, src.getPitch());

            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
            P.p.log(Level.WARNING, "Error encountered while serializing a LazyLocation.");
            return obj;
        }
    }
}
