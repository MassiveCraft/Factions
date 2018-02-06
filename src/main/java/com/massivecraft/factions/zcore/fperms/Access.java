package com.massivecraft.factions.zcore.fperms;

import org.bukkit.ChatColor;

public enum Access {
    ALLOW("&aALLOW"),
    DENY("&4DENY"),
    UNDEFINED("&7UND");

    private String name;

    Access(String name) {
        this.name = name;
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
        return ChatColor.translateAlternateColorCodes('&', this.name);
    }


    @Override
    public String toString() {
        return name();
    }
}
