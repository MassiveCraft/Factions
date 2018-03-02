package com.massivecraft.factions.zcore.fperms;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public enum PermissableAction {
    BAN("ban"),
    BUILD("build"),
    DESTROY("destroy"),
    FROST_WALK("frostwalk"),
    PAIN_BUILD("painbuild"),
    DOOR("door"),
    BUTTON("button"),
    LEVER("lever"),
    CONTAINER("container"),
    INVITE("invite"),
    KICK("kick"),
    ITEM("items"), // generic for most items
    SETHOME("sethome"),
    WITHDRAW("withdraw"),
    TERRITORY("territory"),
    ACCESS("access"),
    DISBAND("disband"),
    PROMOTE("promote"),
    PERMS("perms"),
    SETWARP("setwarp"),
    WARP("warp"),;

    private String name;

    PermissableAction(String name) {
        this.name = name;
    }

    /**
     * Get the friendly name of this action. Used for editing in commands.
     *
     * @return friendly name of the action as a String.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Case insensitive check for action.
     *
     * @param check
     * @return
     */
    public static PermissableAction fromString(String check) {
        for (PermissableAction permissableAction : values()) {
            if (permissableAction.name().equalsIgnoreCase(check)) {
                return permissableAction;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    // Utility method to build items for F Perm GUI
    public ItemStack buildItem(FPlayer fme, Permissable permissable) {
        final ConfigurationSection ACTION_CONFIG = P.p.getConfig().getConfigurationSection("fperm-gui.action");

        String displayName = replacePlaceholers(ACTION_CONFIG.getString("placeholder-item.name"), fme, permissable);
        List<String> lore = new ArrayList<>();

        if (ACTION_CONFIG.getString("materials." + name().toLowerCase().replace('_', '-')) == null) {
            return null;
        }
        Material material = Material.matchMaterial(ACTION_CONFIG.getString("materials." + name().toLowerCase().replace('_', '-')));
        if (material == null) {
            material = Material.STAINED_CLAY;
        }

        Access access = fme.getFaction().getAccess(permissable, this);
        if (access == null) {
            access = Access.UNDEFINED;
        }
        DyeColor dyeColor = null;
        try {
            dyeColor = DyeColor.valueOf(ACTION_CONFIG.getString("access." + access.name().toLowerCase()));
        } catch (Exception exception) {}

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        if (dyeColor != null) {
            item.setDurability(dyeColor.getWoolData());
        }

        for (String loreLine : ACTION_CONFIG.getStringList("placeholder-item.lore")) {
            lore.add(replacePlaceholers(loreLine, fme, permissable));
        }

        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String replacePlaceholers(String string, FPlayer fme, Permissable permissable) {
        // Run Permissable placeholders
        string = permissable.replacePlaceholders(string);

        String actionName = name.substring(0, 1).toUpperCase() + name.substring(1);
        string = string.replace("{action}", actionName);

        Access access = fme.getFaction().getAccess(permissable, this);
        if (access == null) {
            access = Access.UNDEFINED;
        }
        String actionAccess = access.getName();
        string = string.replace("{action-access}", actionAccess);
        string = string.replace("{action-access-color}", access.getColor().toString());

        return string;
    }

}
