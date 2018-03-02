package com.massivecraft.factions.zcore.fperms.gui;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Permissable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class PermissableRelationGUI implements InventoryHolder, PermissionGUI {

    private Inventory relationGUI;
    private FPlayer fme;

    private int guiSize;

    private HashMap<Integer, Permissable> relationSlots = new HashMap<>();

    private final ConfigurationSection RELATION_CONFIG = P.p.getConfig().getConfigurationSection("fperm-gui.relation");


    public PermissableRelationGUI(FPlayer fme) {
        this.fme = fme;

        // Build basic Inventory info
        guiSize = RELATION_CONFIG.getInt("rows", 3);
        if (guiSize > 5) {
            guiSize = 5;
            P.p.log(Level.INFO, "Relation GUI size out of bounds, defaulting to 5");
        }
        guiSize *= 9;
        String guiName = ChatColor.translateAlternateColorCodes('&', RELATION_CONFIG.getString("name", "FactionPermissions"));
        relationGUI = Bukkit.createInventory(this, guiSize, guiName);

        for (String key : RELATION_CONFIG.getConfigurationSection("slots").getKeys(false)) {
            int slot = RELATION_CONFIG.getInt("slots." + key);
            if (slot + 1 > guiSize && slot > 0) {
                P.p.log(Level.WARNING, "Invalid slot of " + key.toUpperCase() + " in relation GUI skipping it");
                continue;
            }

            if (getPermissable(key) == null) {
                P.p.log(Level.WARNING, "Invalid permissable " + key.toUpperCase() + " skipping it");
                continue;
            }

            relationSlots.put(slot, getPermissable(key));
        }

        buildDummyItems();
        buildItems();
    }

    @Override
    public Inventory getInventory() {
        return relationGUI;
    }

    @Override
    public void onClick(int slot, ClickType clickType) {
        if (!relationSlots.containsKey(slot)) {
            return;
        }

        fme.getPlayer().openInventory(new PermissableActionGUI(fme, relationSlots.get(slot)).getInventory());
    }

    private Permissable getPermissable(String name) {
        try {
            return Relation.valueOf(name.toUpperCase());
        } catch (Exception e) {
        }
        try {
            return Role.valueOf(name.toUpperCase());
        } catch (Exception e) {
        }

        return null;
    }

    private void buildItems() {
        for (Map.Entry<Integer, Permissable> entry : relationSlots.entrySet()) {
            Permissable permissable = entry.getValue();

            ItemStack item = permissable.buildItem();

            if (item == null) {
                P.p.log(Level.WARNING, "Invalid material for " + permissable.toString().toUpperCase() + " skipping it");
                continue;
            }

            relationGUI.setItem(entry.getKey(), item);
        }
    }

    private void buildDummyItems() {
        for (String key : RELATION_CONFIG.getConfigurationSection("dummy-items").getKeys(false)) {
            int dummyId;
            try {
                dummyId = Integer.parseInt(key);
            } catch (NumberFormatException exception) {
                P.p.log(Level.WARNING, "Invalid dummy item id: " + key.toUpperCase());
                continue;
            }

            ItemStack dummyItem = buildDummyItem(dummyId);
            if (dummyItem == null) {
                continue;
            }

            List<Integer> dummySlots = RELATION_CONFIG.getIntegerList("dummy-items." + key);
            for (Integer slot : dummySlots) {
                if (slot+1 > guiSize || slot < 0) {
                    P.p.log(Level.WARNING, "Invalid slot: " + slot + " for dummy item: " + key);
                    continue;
                }
                relationGUI.setItem(slot, dummyItem);
            }
        }
    }

    private ItemStack buildDummyItem(int id) {
        final ConfigurationSection DUMMY_CONFIG = P.p.getConfig().getConfigurationSection("fperm-gui.dummy-items." + id);

        Material material = Material.matchMaterial(DUMMY_CONFIG.getString("material", ""));
        if (material == null) {
            P.p.log(Level.WARNING, "Invalid material for dummy item: " + id);
            return null;
        }

        ItemStack itemStack = new ItemStack(material);

        DyeColor color;
        try {
            color = DyeColor.valueOf(DUMMY_CONFIG.getString("color", ""));
        } catch (Exception exception) {
            color = null;
        }
        if (color != null) {
            itemStack.setDurability(color.getWoolData());
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', DUMMY_CONFIG.getString("name", " ")));

        List<String> lore = new ArrayList<>();
        for (String loreLine : DUMMY_CONFIG.getStringList("lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
        }
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
