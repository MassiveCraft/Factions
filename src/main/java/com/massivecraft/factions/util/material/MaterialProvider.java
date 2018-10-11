package com.massivecraft.factions.util.material;

import org.bukkit.Material;

import java.util.HashMap;

public class MaterialProvider {

    private MaterialDb db;
    private HashMap<String, MaterialData> materialData;

    public MaterialProvider(MaterialDb db) {
        this.db = db;
    }

    public Material resolve(String name) {
        return materialData.get(name).get();
    }

    public void setMaterialData(HashMap<String, MaterialData> materialData) {
        this.materialData = materialData;
    }

    public class MaterialData {

        private String name;
        private String legacy;

        public MaterialData(String name, String legacy) {
            this.name = name;
            this.legacy = legacy;
        }

        public Material get() {
            if (!db.legacy) {
                return Material.matchMaterial(name);
            } else {
                return Material.matchMaterial(legacy);
            }
        }

    }

}
