package com.massivecraft.factions.util.material;

import com.google.gson.annotations.SerializedName;
import com.massivecraft.factions.P;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.logging.Level;

public class MaterialProvider {

    protected HashMap<String, MaterialData> materialData;

    MaterialProvider(HashMap<String, MaterialData> materialData) {
        this.materialData = materialData;
    }

    public Material resolve(String name) {
        Material material = materialData.get(name).get();
        if (material == null) {
            // Could not create Material from provided String, return Air
            P.p.log(Level.WARNING, "Invalid material: " + name.toUpperCase());
            return Material.AIR;
        }
        return material;
    }

    public String fromLegacy(String legacy) {
        for (MaterialData data : materialData.values()) {
            if (data.legacy.equalsIgnoreCase(legacy)) {
                return data.name;
            }
        }
        return null;
    }

    public class MaterialData {

        @SerializedName("material")
        private String name;
        private String legacy;

        public MaterialData(String name, String legacy) {
            this.name = name;
            this.legacy = legacy;
        }

        public Material get() {
            if (!MaterialDb.getInstance().legacy) {
                return Material.matchMaterial(name);
            } else {
                if (legacy == null) {
                    // Fallback to the 1.13 name
                    return Material.matchMaterial(name);
                }
                return Material.matchMaterial(legacy);
            }
        }

        @Override
        public String toString() {
            return "MaterialData{" +
                    "name='" + name + '\'' +
                    ", legacy='" + legacy + '\'' +
                    '}';
        }
    }

}
