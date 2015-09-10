package com.massivecraft.factions.struct;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;


public enum Relation {
    MEMBER(4, "member"),
    ALLY(3, "ally"),
    TRUCE(2, "truce"),
    NEUTRAL(1, "neutral"),
    ENEMY(0, "enemy");

    public final int value;
    public final String nicename;

    private Relation(final int value, final String nicename) {
        this.value = value;
        this.nicename = nicename;
    }

    @Override
    public String toString() {
        return this.nicename;
    }

    public static Relation fromString(String s) {
        // Because Java 6 doesn't allow String switches :(
        if (s.equalsIgnoreCase("member")) {
            return MEMBER;
        } else if (s.equalsIgnoreCase("ally")) {
            return ALLY;
        } else if (s.equalsIgnoreCase("truce")) {
            return TRUCE;
        } else if (s.equalsIgnoreCase("enemy")) {
            return ENEMY;
        } else {
            return NEUTRAL; // If they somehow mess things up, go back to default behavior.
        }
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
}
