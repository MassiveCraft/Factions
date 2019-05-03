package com.massivecraft.factions.zcore.gui.items;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

/**
 *  DynamicItem uses ItemGUI merges to handle
 *  specific states in some UI menus, for example
 *
 *  PermissableActionGUI handles individual actions
 *  that have states related to the user
 *  these can change depending on what the UI and the config
 *  provide to this class
 */
public class DynamicItem extends ItemGUI {

    // May contain incomplete ItemGUI but will be used to be merged into this main one
    private HashMap<String, ItemGUI> states = new HashMap<>();

    public DynamicItem(Builder builder, ConfigurationSection section) {
        super(builder);
        for (String key : section.getKeys(false)) {
            // Build a map with all sub states, might be invalid but we won't access them anyways
            states.put(key.toLowerCase(), fromConfig(section.getConfigurationSection(key)));
        }
    }

    public DynamicItem(ItemGUI itemGUI) {
        super(itemGUI);
        if (itemGUI instanceof DynamicItem) {
            this.states = ((DynamicItem) itemGUI).states;
        }
    }

    // Merges the ItemGUI from the state into a clone of this (Clone or it might leave some data from other states)
    public ItemGUI get(String stage) {
        ItemGUI clone = new ItemGUI(this);
        if (states.containsKey(stage)) {
            clone.merge(states.get(stage));
        }
        return clone;
    }

}
