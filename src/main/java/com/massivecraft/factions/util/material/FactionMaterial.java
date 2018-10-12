package com.massivecraft.factions.util.material;

import org.bukkit.Material;

public class FactionMaterial {

    private String name;

    public FactionMaterial(String name) {
        this.name = name;
    }

    public FactionMaterial(Material material) {
        // If we are using Legacy we need to change the name to the 1.13 equivalent
        if (MaterialDb.getInstance().legacy) {
            this.name = MaterialDb.getInstance().provider.fromLegacy(material.name());
        } else {
            this.name = material.name();
        }
    }

    public Material get() {
        return MaterialDb.getInstance().get(name);
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FactionMaterial that = (FactionMaterial) o;
        // Compare provided Materials instead of the name as different names might provide same materials
        return get() == that.get();
    }

    @Override
    public int hashCode() {
        // Use material hashCode instead of name
        return get().hashCode();
    }
}
