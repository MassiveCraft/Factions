package com.massivecraft.factions.util.material;

import org.bukkit.Material;

public class FactionMaterial {

    private String name;

    private FactionMaterial(String name, boolean legacy) {
        if (legacy && MaterialDb.getInstance().legacy) {
            // If we are using Legacy we need to change the name to the 1.13 equivalent
            this.name = MaterialDb.getInstance().provider.fromLegacy(name);
        } else {
            this.name = name;
        }
    }

    private FactionMaterial(Material material) {
        if (MaterialDb.getInstance().legacy) {
            this.name = MaterialDb.getInstance().provider.fromLegacy(material.name());
        } else {
            this.name = material.name();
        }
    }

    // Build FactionMaterial with only 1.13 name
    public static FactionMaterial constant(String name) {
        return new FactionMaterial(name, false);
    }

    // Build FactionMaterial from legacy name (if in legacy mode)
    public static FactionMaterial legacy(String name) {
        return new FactionMaterial(name, true);
    }

    // Build using Material provided
    public static FactionMaterial material(Material material) {
        return new FactionMaterial(material);
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
        // If the MaterialDb is null then it means that Conf is initializing, wait it out.
        // We may not ask the Db for a Material so use the name instead.
        if (MaterialDb.getInstance() == null) {
            return name.equals(that.name);
        }
        // Compare provided Materials instead of the name as different names might provide same materials
        return get() == that.get();
    }

    @Override
    public int hashCode() {
        // Use material hashCode instead of name
        if (MaterialDb.getInstance() == null) {
            return name.hashCode();
        }
        return get().hashCode();
    }
}
