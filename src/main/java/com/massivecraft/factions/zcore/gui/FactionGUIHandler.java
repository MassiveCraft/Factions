package com.massivecraft.factions.zcore.gui;

import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.gui.items.DynamicItem;
import com.massivecraft.factions.zcore.gui.items.ItemGUI;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.logging.Level;

/**
 *  Loads and caches config values as ItemGUI
 *  instances, and then provides them to UI menus
 */
public class FactionGUIHandler {

    private static FactionGUIHandler uiHandler;

    private HashMap<String, ItemGUI> global = new HashMap<>();
    private HashMap<String, ItemGUI> dummies = new HashMap<>();

    public static void start() {
        uiHandler = new FactionGUIHandler();
        uiHandler.build();
    }

    public static FactionGUIHandler instance() {
        return uiHandler;
    }

    public void build() {
        if (!P.p.getConfig().contains("gui")) {
            P.p.log(Level.SEVERE, "Your config GUI section is outdated, because of this it will not work, please update your config");
        }

        // Globals
        ConfigurationSection globalSection = P.p.getConfig().getConfigurationSection("gui.global");
        if (globalSection != null) {
            for (String key : globalSection.getKeys(false)) {
                ConfigurationSection section = globalSection.getConfigurationSection(key);
                ItemGUI itemGUI = ItemGUI.fromConfig(section);
                if (itemGUI != null) {
                    global.put(key, itemGUI);
                }
            }
        }

        // Dummies
        ConfigurationSection dummiesSection = P.p.getConfig().getConfigurationSection("gui.dummies");
        if (dummiesSection != null) {
            for (String key : dummiesSection.getKeys(false)) {
                ConfigurationSection section = dummiesSection.getConfigurationSection(key);
                ItemGUI itemGUI = ItemGUI.fromConfig(section);
                if (itemGUI != null && itemGUI.isValid()) {
                    dummies.put(key, itemGUI);
                }
            }
        }
    }

    public ItemGUI mergeBase(String id, ConfigurationSection object) {
        ItemGUI base = getBaseItem(id);
        if (base == null) {
            return null;
        }
        base.merge(ItemGUI.fromConfig(object));
        return merge(base, object);
    }

    public ItemGUI mergeDummyBase(String id, ConfigurationSection object) {
        ItemGUI base = getDummyItem(id);
        return merge(base, object);
    }

    private ItemGUI merge(ItemGUI base, ConfigurationSection object) {
        if (base == null) {
            return null;
        }
        base.merge(ItemGUI.fromConfig(object));
        return base;
    }

    public ItemGUI getBaseItem(String id) {
        if (global.get(id) == null) {
            return null;
        } else {
            ItemGUI itemGUI = global.get(id);
            if (itemGUI instanceof DynamicItem) {
                return new DynamicItem(itemGUI);
            }
            return new ItemGUI(itemGUI);
        }
    }

    public ItemGUI getDummyItem(String id) {
        if (dummies.get(id) == null) {
            return null;
        } else {
            return new ItemGUI(dummies.get(id));
        }
    }

}
