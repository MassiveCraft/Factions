package com.massivecraft.factions.util.material;

import org.bukkit.Material;

import java.util.Objects;

public class FactionMaterial {

    private String name;

    public FactionMaterial(String name) {
        this.name = name;
    }

    public FactionMaterial(Material material) {
        this.name = material.name();
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
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
