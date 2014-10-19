package com.massivecraft.factions.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class MiscUtil {

    public static EntityType creatureTypeFromEntity(Entity entity) {
        if (!(entity instanceof Creature)) {
            return null;
        }

        String name = entity.getClass().getSimpleName();
        name = name.substring(5); // Remove "Craft"

        return EntityType.fromName(name);
    }

    // Inclusive range
    public static long[] range(long start, long end) {
        long[] values = new long[(int) Math.abs(end - start) + 1];

        if (end < start) {
            long oldstart = start;
            start = end;
            end = oldstart;
        }

        for (long i = start; i <= end; i++) {
            values[(int) (i - start)] = i;
        }

        return values;
    }

    /// TODO create tag whitelist!!
    public static HashSet<String> substanceChars = new HashSet<String>(Arrays.asList(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"}));

    public static String getComparisonString(String str) {
        String ret = "";

        str = ChatColor.stripColor(str);
        str = str.toLowerCase();

        for (char c : str.toCharArray()) {
            if (substanceChars.contains(String.valueOf(c))) {
                ret += c;
            }
        }
        return ret.toLowerCase();
    }

    public static ArrayList<String> validateTag(String str) {
        ArrayList<String> errors = new ArrayList<String>();

        if (getComparisonString(str).length() < Conf.factionTagLengthMin) {
            errors.add(P.p.txt.parse("<i>The faction tag can't be shorter than <h>%s<i> chars.", Conf.factionTagLengthMin));
        }

        if (str.length() > Conf.factionTagLengthMax) {
            errors.add(P.p.txt.parse("<i>The faction tag can't be longer than <h>%s<i> chars.", Conf.factionTagLengthMax));
        }

        for (char c : str.toCharArray()) {
            if (!substanceChars.contains(String.valueOf(c))) {
                errors.add(P.p.txt.parse("<i>Faction tag must be alphanumeric. \"<h>%s<i>\" is not allowed.", c));
            }
        }

        return errors;
    }
}

