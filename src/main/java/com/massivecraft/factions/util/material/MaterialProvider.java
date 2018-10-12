package com.massivecraft.factions.util.material;

import com.google.gson.annotations.SerializedName;
import com.massivecraft.factions.P;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.logging.Level;

public class MaterialProvider {

    protected HashMap<String, MaterialData> materialData;

    public MaterialProvider(HashMap<String, MaterialData> materialData) {
        this.materialData = materialData;
    }

    public Material resolve(String name) {
        Material material = materialData.get(name).get();
        if (material == null) {
            P.p.log(Level.WARNING, "Invalid Material: " + name.toUpperCase());
            return Material.AIR;
        }
        return material;
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
                    return null;
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
