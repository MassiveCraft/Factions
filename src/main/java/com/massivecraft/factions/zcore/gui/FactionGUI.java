package com.massivecraft.factions.zcore.gui;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.gui.items.DynamicItem;
import com.massivecraft.factions.zcore.gui.items.ItemGUI;
import com.massivecraft.factions.zcore.util.TagUtil;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public abstract class FactionGUI<T> implements InventoryHolder {

    protected Inventory inventory;

    protected int size;
    private int back = -1;

    private Map<Integer, T> slotMap = new HashMap<>();

    protected FPlayer user;
    protected ConfigurationSection config;

    public FactionGUI(String path, FPlayer user) {
        this.user = user;
        this.config = P.p.getConfig().getConfigurationSection(path);
    }

    // Convert a config String to Type
    protected abstract T convert(String key);

    // Convert a Type to a config String
    protected abstract String convert(T type);

    // Parse all the placeholder values in this String, will be injected into the ItemGUI and return it
    protected abstract String parse(String toParse, T type);

    protected abstract void onClick(T action, ClickType clickType);

    // Should only be called by the InventoryListener
    public void click(int slot, ClickType clickType) {
        if (slotMap.containsKey(slot)) {
            onClick(slotMap.get(slot), clickType);
        } else if (this instanceof Backable && back != -1) {
            ((Backable) this).onBack();
        }
    }

    public void build() {
        if (config == null) {
            P.p.log(Level.WARNING, "Attempted to build GUI for %s but config section not present.", getClass().getName());
            P.p.log(Level.WARNING, "Copy your config, allow the section to generate, then copy it back to your old config.");
            return;
        }

        // Build basic Inventory info
        size = config.getInt("rows", 3);
        if (size > 6) {
            size = 6;
            P.p.log(Level.WARNING, "UI size out of bounds, defaulting to 6.");
        }
        size *= 9;

        String guiName = parseDefault(config.getString("name", "FactionGUI"));
        inventory = Bukkit.createInventory(this, size, guiName);

        // Generates a map that relates | int slot -> T type
        // Does not handle any actual UI
        for (String key : config.getConfigurationSection("slots").getKeys(false)) {
            int slot = config.getInt("slots." + key);
            if (slot >= size || slot < 0) {
                P.p.log(Level.WARNING, "Slot out of bounds: " + key.toUpperCase());
                continue;
            }

            T type = convert(key);
            if (convert(key) == null) {
                P.p.log(Level.WARNING, "Invalid type: " + key.toUpperCase());
                continue;
            }

            slotMap.put(slot, type);
        }

        buildDummyItems();
        buildItems();
    }

    protected void buildItems() {
        ConfigurationSection items = config.getConfigurationSection("items");
        if (items == null) {
            P.p.log(Level.WARNING, "Attempted to build GUI for %s but item section not present", getClass().getName());
            return;
        }
        for (Map.Entry<Integer, T> entry : slotMap.entrySet()) {
            T type = entry.getValue();

            ItemGUI item;
            String base = items.getString(convert(type));
            // If this is null we need to merge it
            if (!items.isString(convert(type))) {
                base = items.getString(convert(type) + ".base");
                // Still null, skip it
                if (base == null) {
                    continue;
                }
                item = FactionGUIHandler.instance().mergeBase(base, items.getConfigurationSection(convert(type)));
            } else {
                item = FactionGUIHandler.instance().getBaseItem(base);
            }
            // Something went wrong in the merging or getting phase (probably does not exist)
            if (item == null) {
                continue;
            }
            if (this instanceof Dynamic && item instanceof DynamicItem) {
                String state = getState(type);
                // Merge the state item into the base
                item = ((DynamicItem) item).get(state);
            }
            item = parse(item, type);

            inventory.setItem(entry.getKey(), item.get());
        }
    }

    protected void buildDummyItems() {
        ConfigurationSection dummies = config.getConfigurationSection("dummies");
        if (dummies == null) {
            P.p.log(Level.WARNING, "Attempted to build GUI for %s but dummies section not present", getClass().getName());
            return;
        }
        for (String key : dummies.getKeys(false)) {
            int slot;
            try {
                slot = Integer.parseInt(key);
            } catch (NumberFormatException e) {
                continue;
            }
            if (slot >= size || slot < 0 || slotMap.containsKey(slot)) {
                continue;
            }

            ItemGUI item;
            String base = dummies.getString(key);
            if (base.equalsIgnoreCase("back") && this instanceof Backable) {
                back = slot;
            }
            // If this is null we need to merge it
            if (!dummies.isString(key)) {
                base = dummies.getString(key + ".base");
                // Still null, skip it
                if (base == null) {
                    continue;
                }
                item = FactionGUIHandler.instance().mergeDummyBase(base, dummies.getConfigurationSection(key));
            } else {
                item = FactionGUIHandler.instance().getDummyItem(base);
            }
            // Something went wrong in the merging or getting phase (probably does not exist)
            if (item == null) {
                continue;
            }

            item = parse(item, null);

            inventory.setItem(slot, item.get());
        }
    }

    public String getState(T type) {
        return null;
    }

    // Will parse default faction stuff, ie: Faction Name, Power, Colors etc
    protected ItemGUI parse(ItemGUI itemGUI, T type) {
        itemGUI.setName(parseDefault(itemGUI.getName()));
        if (type != null) {
            itemGUI.setName(parse(itemGUI.getName(), type));
        }
        itemGUI.setLore(parseList(itemGUI.getLore(), type));

        return itemGUI;
    }

    protected List<String> parseList(List<String> stringList, T type) {
        List<String> newList = new ArrayList<>();
        for (String toParse : stringList) {
            String parsed = parseDefault(toParse);
            if (type != null) {
                parsed = parse(parsed, type);
            }
            newList.add(parsed);
        }
        return newList;
    }

    protected String parseDefault(String toParse) {
        toParse = TextUtil.parseColor(toParse);
        toParse = TagUtil.parsePlain(user, toParse);
        toParse = TagUtil.parsePlain(user.getFaction(), toParse);
        return TagUtil.parsePlaceholders(user.getPlayer(), toParse);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open() {
        user.getPlayer().openInventory(getInventory());
    }

    public interface Dynamic {}

    public interface Backable {

        void onBack();

    }

}