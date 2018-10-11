package com.massivecraft.factions.util.material;

import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.P;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;

public class MaterialDb {

    /*

    This utility has no concept of block metadata, converts if necessary 1.13
    material names to < 1.12 materials, or keeps 1.13 materials.

    Useful as we don't really need extra metadata for stuff like territory block breaking checking.

        "ACACIA_BOAT": {
            "name": "ACACIA_BOAT",
            "legacy": "BOAT_ACACIA"
        }

     */

    private static MaterialDb instance;

    public boolean legacy = true;
    private MaterialProvider provider;

    public MaterialDb() {
        instance = this;
        load();
    }

    public Material get(String name) {
        return provider.resolve(name);
    }

    public void load() {
        try {
            String versionString = Bukkit.getVersion();
            String version = versionString.substring(versionString.indexOf('.') + 1, versionString.lastIndexOf('.'));
            legacy = Integer.parseInt(version) < 13;
        } catch (NumberFormatException e) {
            // Issue formatting major version integer... uhm
            legacy = true;
        }


        InputStreamReader reader = new InputStreamReader(P.p.getResource("materials.json"));
        Type typeToken = new TypeToken<HashMap<String, MaterialProvider.MaterialData>>(){}.getType();
        HashMap<String, MaterialProvider.MaterialData> materialData = P.p.gson.fromJson(reader, typeToken);
        provider = new MaterialProvider(materialData);
    }

    public static MaterialDb getInstance() {
        return instance;
    }

}
