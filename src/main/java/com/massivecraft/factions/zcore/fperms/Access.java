package com.massivecraft.factions.zcore.fperms;

import org.bukkit.ChatColor;

public enum Access {
    ALLOW("Allow", ChatColor.GREEN),
    DENY("Deny", ChatColor.DARK_RED),
    UNDEFINED("Undefined", ChatColor.GRAY);

    private String name;
    private ChatColor color;

    Access(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Case insensitive check for access.
     *
     * @param check
     * @return
     */
    public static Access fromString(String check) {
        for (Access access : values()) {
            if (access.name().equalsIgnoreCase(check)) {
                return access;
            }
        }

        return null;
    }

    public String getName() {
        return this.name;
    }

    public ChatColor getColor() {
        return color;
    }

    @Override
    public String toString() {
        return name();
    }

}
