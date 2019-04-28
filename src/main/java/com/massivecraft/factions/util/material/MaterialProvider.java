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
        if (name == null) {
            P.p.log("Null material name, Skipping");
            return Material.AIR;
        }

        if (!materialData.containsKey(name)) {
            P.p.log(Level.INFO, "Material does not exist: " + name.toUpperCase());
            return Material.AIR;
        }

        Material material = materialData.get(name).get();
        if (material == null) {
            // Could not create Material from provided String, return Air
            P.p.log(Level.INFO, "Invalid material: " + name.toUpperCase());
            return Material.AIR;
        }
        return material;
    }

    public boolean isLegacy(String legacy) {
        // If we don't have it as key then it is Legacy or doesn't exist
        return !materialData.containsKey(legacy);
    }

    public String fromLegacy(String legacy) {
        for (MaterialData data : materialData.values()) {
            if (data.legacy != null && data.legacy.equalsIgnoreCase(legacy)) {
                return data.name;
            } else if (data.name.equalsIgnoreCase(legacy)) {
                // If legacy doesn't match but name does return it
                return data.name;
            }
        }
        return null;
    }

    public boolean isSign(Material mat) {
        return mat.name().toUpperCase().contains("SIGN");
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
