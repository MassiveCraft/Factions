package com.massivecraft.factions.zcore.fperms.gui;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
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

import java.util.*;
import java.util.logging.Level;

public class PermissableActionGUI implements InventoryHolder, PermissionGUI {

    private Inventory actionGUI;
    private FPlayer fme;

    private int guiSize;

    private Permissable permissable;

    private HashMap<Integer, PermissableAction> actionSlots = new HashMap<>();
    private HashMap<Integer, SpecialItem> specialSlots = new HashMap<>();
    private ArrayList<Integer> usedDummySlots = new ArrayList<>();

    private final ConfigurationSection section;

    public PermissableActionGUI(FPlayer fme, Permissable permissable) {
        this.fme = fme;
        this.permissable = permissable;
        this.section = P.p.getConfig().getConfigurationSection("fperm-gui.action");
    }

    public void build() {
        if (section == null) {
            P.p.log(Level.WARNING, "Attempted to build f perm GUI but config section not present.");
            P.p.log(Level.WARNING, "Copy your config, allow the section to generate, then copy it back to your old config.");
            return;
        }

        guiSize = section.getInt("rows", 3);
        if (guiSize > 5) {
            guiSize = 5;
            P.p.log(Level.INFO, "Action GUI size out of bounds, defaulting to 5");
        }

        guiSize *= 9;
        String guiName = ChatColor.translateAlternateColorCodes('&', section.getString("name", "FactionPerms"));
        actionGUI = Bukkit.createInventory(this, guiSize, guiName);

        for (String key : section.getConfigurationSection("slots").getKeys(false)) {
            int slot = section.getInt("slots." + key);
            if (slot + 1 > guiSize || slot < 0) {
                P.p.log(Level.WARNING, "Invalid slot for: " + key.toUpperCase());
                continue;
            }

            if (SpecialItem.isSpecial(key)) {
                specialSlots.put(slot, SpecialItem.fromString(key));
                continue;
            }

            PermissableAction permissableAction = PermissableAction.fromString(key.toUpperCase().replace('-', '_'));
            if (permissableAction == null) {
                P.p.log(Level.WARNING, "Invalid permissable action: " + key.toUpperCase());
                continue;
            }

            actionSlots.put(section.getInt("slots." + key), permissableAction);
        }

        buildDummyItems();

        if (actionSlots.values().toArray().length != PermissableAction.values().length) {
            // Missing actions add them forcefully to the GUI and log error
            Set<PermissableAction> missingActions = new HashSet<>(Arrays.asList(PermissableAction.values()));
            missingActions.removeAll(actionSlots.values());

            for (PermissableAction action : missingActions) {
                if (!usedDummySlots.isEmpty()) {
                    int slot = usedDummySlots.get(0);
                    actionSlots.put(slot, action);
                } else {
                    int slot = actionGUI.firstEmpty();
                    if (slot != -1) {
                        actionSlots.put(slot, action);
                    }
                }
                P.p.log(Level.WARNING, "Missing action: " + action.name());
            }

        }

        buildSpecialItems();
        buildItems();
    }

    @Override
    public Inventory getInventory() {
        return actionGUI;
    }

    @Override
    public void onClick(int slot, ClickType click) {
        if (specialSlots.containsKey(slot)) {
            if (specialSlots.get(slot) == SpecialItem.BACK) {
                PermissableRelationGUI relationGUI = new PermissableRelationGUI(fme);
                relationGUI.build();

                fme.getPlayer().openInventory(relationGUI.getInventory());
            }
            return;
        }
        if (!actionSlots.containsKey(slot)) {
            return;
        }

        PermissableAction action = actionSlots.get(slot);
        Access access;
        if (click == ClickType.LEFT) {
            access = Access.ALLOW;
            fme.getFaction().setPermission(permissable, action, access);
        } else if (click == ClickType.RIGHT) {
            access = Access.DENY;
            fme.getFaction().setPermission(permissable, action, access);
        } else if (click == ClickType.MIDDLE) {
            access = Access.UNDEFINED;
            fme.getFaction().setPermission(permissable, action, access);
        } else {
            return;
        }

        actionGUI.setItem(slot, action.buildItem(fme, permissable));
        fme.msg(TL.COMMAND_PERM_SET, action.name(), access.name(), permissable.name());
        P.p.log(String.format(TL.COMMAND_PERM_SET.toString(), action.name(), access.name(), permissable.name()) + " for faction " + fme.getTag());
    }

    private void buildItems() {
        for (Map.Entry<Integer, PermissableAction> entry : actionSlots.entrySet()) {
            PermissableAction permissableAction = entry.getValue();

            ItemStack item = permissableAction.buildItem(fme, permissable);

            if (item == null) {
                P.p.log(Level.WARNING, "Invalid item for: " + permissableAction.toString().toUpperCase());
                continue;
            }

            actionGUI.setItem(entry.getKey(), item);
        }
    }

    private void buildSpecialItems() {
        for (Map.Entry<Integer, SpecialItem> entry : specialSlots.entrySet()) {
            actionGUI.setItem(entry.getKey(), getSpecialItem(entry.getValue()));
        }
    }

    private ItemStack getSpecialItem(SpecialItem specialItem) {
        if (section == null) {
            P.p.log(Level.WARNING, "Attempted to build f perm GUI but config section not present.");
            P.p.log(Level.WARNING, "Copy your config, allow the section to generate, then copy it back to your old config.");
            return new ItemStack(Material.AIR);
        }

        switch (specialItem) {
            case RELATION:
                return permissable.buildItem();
            case BACK:
                ConfigurationSection backButtonConfig = P.p.getConfig().getConfigurationSection("fperm-gui.back-item");

                ItemStack backButton = new ItemStack(Material.matchMaterial(backButtonConfig.getString("material")));
                ItemMeta backButtonMeta = backButton.getItemMeta();

                backButtonMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', backButtonConfig.getString("name")));
                List<String> lore = new ArrayList<>();
                for (String loreLine : backButtonConfig.getStringList("lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
                }
                backButtonMeta.setLore(lore);

                backButton.setItemMeta(backButtonMeta);

                return backButton;
            default:
                return null;
        }
    }

    private void buildDummyItems() {
        if (section == null) {
            P.p.log(Level.WARNING, "Attempted to build f perm GUI but config section not present.");
            P.p.log(Level.WARNING, "Copy your config, allow the section to generate, then copy it back to your old config.");
            return;
        }

        for (String key : section.getConfigurationSection("dummy-items").getKeys(false)) {
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

            List<Integer> dummySlots = section.getIntegerList("dummy-items." + key);
            for (Integer slot : dummySlots) {
                if (slot + 1 > guiSize || slot < 0) {
                    P.p.log(Level.WARNING, "Invalid slot: " + slot + " for dummy item: " + key);
                    continue;
                }
                usedDummySlots.add(slot);
                actionGUI.setItem(slot, dummyItem);
            }
        }
    }

    private ItemStack buildDummyItem(int id) {
        final ConfigurationSection dummySection = P.p.getConfig().getConfigurationSection("fperm-gui.dummy-items." + id);

        if (dummySection == null) {
            P.p.log(Level.WARNING, "Attempted to build dummy items for F PERM GUI but config section not present.");
            P.p.log(Level.WARNING, "Copy your config, allow the section to generate, then copy it back to your old config.");
            return new ItemStack(Material.AIR);
        }

        Material material = Material.matchMaterial(dummySection.getString("material", ""));
        if (material == null) {
            P.p.log(Level.WARNING, "Invalid material for dummy item: " + id);
            return null;
        }

        ItemStack itemStack = new ItemStack(material);

        DyeColor color;
        try {
            color = DyeColor.valueOf(dummySection.getString("color", ""));
        } catch (Exception exception) {
            color = null;
        }
        if (color != null) {
            itemStack.setDurability(color.getWoolData());
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dummySection.getString("name", " ")));

        List<String> lore = new ArrayList<>();
        for (String loreLine : dummySection.getStringList("lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
        }
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
