package com.massivecraft.factions.util.material.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.massivecraft.factions.util.material.FactionMaterial;
import org.bukkit.Material;

import java.io.IOException;

public class MaterialAdapter extends TypeAdapter<Material> {

    // Use FactionMaterial as an adapter from: Material <-> Name
    @Override
    public void write(JsonWriter out, Material value) throws IOException {
        // Save 1.13 name for this Material
        out.value(FactionMaterial.material(value).name());
    }

    @Override
    public Material read(JsonReader in) throws IOException {
        return FactionMaterial.from(in.nextString()).get();
    }

}
