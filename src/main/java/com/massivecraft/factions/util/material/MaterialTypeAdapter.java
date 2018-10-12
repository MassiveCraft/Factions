package com.massivecraft.factions.util.material;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class MaterialTypeAdapter extends TypeAdapter<FactionMaterial> {

    @Override
    public void write(JsonWriter out, FactionMaterial value) throws IOException {
        out.value(value.name());
    }

    @Override
    public FactionMaterial read(JsonReader in) throws IOException {
        return new FactionMaterial(in.nextString());
    }

}
