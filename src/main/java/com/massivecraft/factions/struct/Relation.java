package com.massivecraft.factions.struct;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public enum Relation implements Permissable {
    MEMBER(4, TL.RELATION_MEMBER_SINGULAR.toString()),
    ALLY(3, TL.RELATION_ALLY_SINGULAR.toString()),
    TRUCE(2, TL.RELATION_TRUCE_SINGULAR.toString()),
    NEUTRAL(1, TL.RELATION_NEUTRAL_SINGULAR.toString()),
    ENEMY(0, TL.RELATION_ENEMY_SINGULAR.toString());

    public final int value;
    public final String nicename;

    Relation(final int value, final String nicename) {
        this.value = value;
        this.nicename = nicename;
    }

    public static Relation fromString(String s) {
        // Because Java 6 doesn't allow String switches :(
        if (s.equalsIgnoreCase(MEMBER.nicename)) {
            return MEMBER;
        } else if (s.equalsIgnoreCase(ALLY.nicename)) {
            return ALLY;
        } else if (s.equalsIgnoreCase(TRUCE.nicename)) {
            return TRUCE;
        } else if (s.equalsIgnoreCase(ENEMY.nicename)) {
            return ENEMY;
        } else {
            return NEUTRAL; // If they somehow mess things up, go back to default behavior.
        }
    }

    @Override
    public String toString() {
        return this.nicename;
    }

    public String getTranslation() {
        try {
            return TL.valueOf("RELATION_" + name() + "_SINGULAR").toString();
        } catch (IllegalArgumentException e) {
            return toString();
        }
    }

    public String getPluralTranslation() {
        for (TL t : TL.values()) {
            if (t.name().equalsIgnoreCase("RELATION_" + name() + "_PLURAL")) {
                return t.toString();
            }
        }
        return toString();
    }

    public boolean isMember() {
        return this == MEMBER;
    }

    public boolean isAlly() {
        return this == ALLY;
    }

    public boolean isTruce() {
        return this == TRUCE;
    }

    public boolean isNeutral() {
        return this == NEUTRAL;
    }

    public boolean isEnemy() {
        return this == ENEMY;
    }

    public boolean isAtLeast(Relation relation) {
        return this.value >= relation.value;
    }

    public boolean isAtMost(Relation relation) {
        return this.value <= relation.value;
    }

    public ChatColor getColor() {
        if (this == MEMBER) {
            return Conf.colorMember;
        } else if (this == ALLY) {
            return Conf.colorAlly;
        } else if (this == NEUTRAL) {
            return Conf.colorNeutral;
        } else if (this == TRUCE) {
            return Conf.colorTruce;
        } else {
            return Conf.colorEnemy;
        }
    }

    // return appropriate Conf setting for DenyBuild based on this relation and their online status
    public boolean confDenyBuild(boolean online) {
        if (isMember()) {
            return false;
        }

        if (online) {
            if (isEnemy()) {
                return Conf.territoryEnemyDenyBuild;
            } else if (isAlly()) {
                return Conf.territoryAllyDenyBuild;
            } else if (isTruce()) {
                return Conf.territoryTruceDenyBuild;
            } else {
                return Conf.territoryDenyBuild;
            }
        } else {
            if (isEnemy()) {
                return Conf.territoryEnemyDenyBuildWhenOffline;
            } else if (isAlly()) {
                return Conf.territoryAllyDenyBuildWhenOffline;
            } else if (isTruce()) {
                return Conf.territoryTruceDenyBuildWhenOffline;
            } else {
                return Conf.territoryDenyBuildWhenOffline;
            }
        }
    }

    // return appropriate Conf setting for PainBuild based on this relation and their online status
    public boolean confPainBuild(boolean online) {
        if (isMember()) {
            return false;
        }

        if (online) {
            if (isEnemy()) {
                return Conf.territoryEnemyPainBuild;
            } else if (isAlly()) {
                return Conf.territoryAllyPainBuild;
            } else if (isTruce()) {
                return Conf.territoryTrucePainBuild;
            } else {
                return Conf.territoryPainBuild;
            }
        } else {
            if (isEnemy()) {
                return Conf.territoryEnemyPainBuildWhenOffline;
            } else if (isAlly()) {
                return Conf.territoryAllyPainBuildWhenOffline;
            } else if (isTruce()) {
                return Conf.territoryTrucePainBuildWhenOffline;
            } else {
                return Conf.territoryPainBuildWhenOffline;
            }
        }
    }

    // return appropriate Conf setting for DenyUseage based on this relation
    public boolean confDenyUseage() {
        if (isMember()) {
            return false;
        } else if (isEnemy()) {
            return Conf.territoryEnemyDenyUseage;
        } else if (isAlly()) {
            return Conf.territoryAllyDenyUseage;
        } else if (isTruce()) {
            return Conf.territoryTruceDenyUseage;
        } else {
            return Conf.territoryDenyUseage;
        }
    }

    public double getRelationCost() {
        if (isEnemy()) {
            return Conf.econCostEnemy;
        } else if (isAlly()) {
            return Conf.econCostAlly;
        } else if (isTruce()) {
            return Conf.econCostTruce;
        } else {
            return Conf.econCostNeutral;
        }
    }

    // Utility method to build items for F Perm GUI
    @Override
    public ItemStack buildItem() {
        final ConfigurationSection RELATION_CONFIG = P.p.getConfig().getConfigurationSection("fperm-gui.relation");

        String displayName = replacePlaceholders(RELATION_CONFIG.getString("placeholder-item.name", ""));
        List<String> lore = new ArrayList<>();

        Material material = Material.matchMaterial(RELATION_CONFIG.getString("materials." + name().toLowerCase()));
        if (material == null) {
            return null;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        for (String loreLine : RELATION_CONFIG.getStringList("placeholder-item.lore")) {
            lore.add(replacePlaceholders(loreLine));
        }

        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String replacePlaceholders(String string) {
        string = ChatColor.translateAlternateColorCodes('&', string);

        String permissableName = nicename.substring(0, 1).toUpperCase() + nicename.substring(1);

        string = string.replace("{relation-color}", getColor().toString());
        string = string.replace("{relation}", permissableName);

        return string;
    }
}
