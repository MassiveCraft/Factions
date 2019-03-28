package com.massivecraft.factions.util.material;

import com.massivecraft.factions.P;
import org.bukkit.Material;

import java.util.logging.Level;

public class FactionMaterial {

    private String name;

    private FactionMaterial(String name) {
        if (MaterialDb.getInstance().provider.isLegacy(name)) {
            // If the name is legacy attempt to match it to 1.13 name and store that
            this.name = MaterialDb.getInstance().provider.fromLegacy(name);
            if (this.name == null) {
                P.p.log(Level.WARNING, "Material " + name + "does not exist");
            }
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

    // Build FactionMaterial with 1.13 or 1.12 name
    public static FactionMaterial from(String name) {
        return new FactionMaterial(name);
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
