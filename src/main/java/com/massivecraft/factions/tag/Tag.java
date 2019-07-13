package com.massivecraft.factions.tag;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public interface Tag {
    int ARBITRARY_LIMIT = 25000;

    /**
     * Replaces all variables in a plain raw line for a faction
     *
     * @param faction for faction
     * @param line raw line from config with variables to replace for
     * @return clean line
     */
    static String parsePlain(Faction faction, String line) {
        return GeneralTag.parse(FactionTag.parse(line, faction));
    }

    /**
     * Replaces all variables in a plain raw line for a player
     *
     * @param fplayer for player
     * @param line raw line from config with variables to replace for
     * @return clean line
     */
    static String parsePlain(FPlayer fplayer, String line) {
        return parsePlain(fplayer.getFaction(), fplayer, line);
    }

    /**
     * Replaces all variables in a plain raw line for a faction, using relations from fplayer
     *
     * @param faction for faction
     * @param fplayer from player
     * @param line raw line from config with variables to replace for
     * @return clean line
     */
    static String parsePlain(Faction faction, FPlayer fplayer, String line) {
        return GeneralTag.parse(PlayerTag.parse(FactionTag.parse(line, faction, fplayer), fplayer));
    }

    static String parsePlaceholders(Player player, String line) {
        if (player == null || line == null) {
            return line;
        }

        if (P.p.isClipPlaceholderAPIHooked() && player.isOnline()) {
            line = PlaceholderAPI.setPlaceholders(player, line);
        }

        if (P.p.isMVdWPlaceholderAPIHooked() && player.isOnline()) {
            line = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, line);
        }

        return line;
    }

    static boolean isMinimalShow() {
        return P.p.getConfig().getBoolean("minimal-show", false);
    }

    /**
     * Gets the Tag's string representation.
     *
     * @return tag
     */
    String getTag();

    /**
     * Gets if the Tag can be found in the given String.
     *
     * @param test string to test
     * @return true if the tag is found in this string
     */
    boolean foundInString(String test);
}
