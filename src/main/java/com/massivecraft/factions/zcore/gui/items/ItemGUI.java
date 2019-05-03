package com.massivecraft.factions.zcore.gui.items;

import com.massivecraft.factions.P;
import com.massivecraft.factions.util.material.FactionMaterial;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.logging.Level;

/**
 *  This class implements an easy way to map
 *  config values like: materials, lore, name, color
 *  to a respective ItemStack, also has utility methods
 *  to merge, clone, etc
 */
public class ItemGUI {

    private String name;
    private List<String> lore;
    private Material material;
    private DyeColor color;

    public ItemGUI(Builder builder) {
        this.name = builder.name;
        this.lore = builder.lore;
        this.material = builder.material;
        this.color = builder.color;
    }

    public ItemGUI(ItemGUI itemGUI) {
        this.name = itemGUI.name;
        this.lore = itemGUI.lore;
        this.material = itemGUI.material;
        this.color = itemGUI.color;
    }

    public ItemStack get() {
        if (isValid()) {
            ItemStack itemStack = new ItemStack(material);
            ItemMeta meta = itemStack.getItemMeta();

            if (name != null) {
                meta.setDisplayName(name);
            }
            // Empty list if not specified
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

            // If a user places a color, they should be expected to put a colorable object
            if (color != null) {
                // ItemStack.setData() does not work :(
                itemStack.setDurability(color.getWoolData());
            }

            itemStack.setItemMeta(meta);
            return itemStack;
        } else {
            return new ItemStack(Material.AIR);
        }
    }

    public static ItemGUI fromConfig(ConfigurationSection section) {
        Builder builder = new Builder();
        // Allowing null values to pass, to facilitate merging. Getting an ItemStack does not work with essential values being null
        builder.setName(section.getString("name"));
        builder.setLore(section.getStringList("lore"));

        if (section.isString("material")) {
            Material material = FactionMaterial.from(section.getString("material")).get();
            builder.setMaterial(material);
        } else {
            builder.setMaterial(null);
        }

        String colorName = section.getString("color");
        if (colorName != null) {
            try {
                builder.setColor(DyeColor.valueOf(colorName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                P.p.log(Level.WARNING, "Invalid Color: " + colorName);
            }
        }

        if (section.isConfigurationSection("states")) {
            return new DynamicItem(builder, section.getConfigurationSection("states"));
        }

        return builder.build();
    }

    // All non null values in 'from' will be merged into this ItemGUI
    public void merge(ItemGUI from) {
        if (from.material != null) {
            material = from.material;
        }
        if (from.name != null) {
            name = from.name;
        }
        if (from.color != null) {
            color = from.color;
        }
        if (!from.lore.isEmpty()) {
            lore = from.lore;
        }
    }

    public boolean isValid() {
        // For an ItemStack to be built this class needs the material, if more information is available then it will be used
        return material != null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public DyeColor getColor() {
        return color;
    }

    public void setColor(DyeColor color) {
        this.color = color;
    }


    public static class Builder {

        private Material material;
        private String name;
        private List<String> lore;
        private DyeColor color;

        public Builder setColor(DyeColor color) {
            this.color = color;
            return this;
        }

        public Builder setLore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setMaterial(Material material) {
            this.material = material;
            return this;
        }

        public ItemGUI build() {
            return new ItemGUI(this);
        }

    }

}
