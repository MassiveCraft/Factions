/* 
 * Copyright (C) 2013 drtshock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.massivecraft.factions.zcore.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * An enum for requesting strings from the language file.
 */
public enum TL {
    TITLE("title", "&bFactions &0|&r"),
    WILDERNESS("wilderness", "&2Wilderness"),
    WILDERNESS_DESCRIPTION("wilderness-description", ""),
    WARZONE("warzone", "&4Warzone"),
    WARZONE_DESCRIPTION("warzone-description", "Not the safest place to be."),
    SAFEZONE("safezone", "&6Safezone"),
    SAFEZONE_DESCRIPTION("safezone-description", "Free from pvp and monsters."),
    TOGGLE_SB("toggle-sb", "You now have scoreboards set to {value}"),
    DEFAULT_PREFIX("default-prefix", "{relationcolor}[{faction}] &r");

    private String path;
    private String def;
    private static YamlConfiguration LANG;

    /**
     * Lang enum constructor.
     *
     * @param path  The string path.
     * @param start The default string.
     */
    TL(String path, String start) {
        this.path = path;
        this.def = start;
    }

    /**
     * Set the {@code YamlConfiguration} to use.
     *
     * @param config The config to set.
     */
    public static void setFile(YamlConfiguration config) {
        LANG = config;
    }

    @Override
    public String toString() {
        return this == TITLE ? ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def)) + " " : ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def));
    }

    /**
     * Get the default value of the path.
     *
     * @return The default value of the path.
     */
    public String getDefault() {
        return this.def;
    }

    /**
     * Get the path to the string.
     *
     * @return The path to the string.
     */
    public String getPath() {
        return this.path;
    }
}