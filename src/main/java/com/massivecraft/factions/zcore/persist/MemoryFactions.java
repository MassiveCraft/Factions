package com.massivecraft.factions.zcore.persist;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MemoryFactions extends Factions {
    public final Map<String, Faction> factions = new ConcurrentHashMap<String, Faction>();
    public int nextId = 1;

    public void load() {
        // Make sure the default neutral faction exists
        if (!factions.containsKey("0")) {
            Faction faction = generateFactionObject("0");
            factions.put("0", faction);
            faction.setTag(TL.WILDERNESS.toString());
            faction.setDescription(TL.WILDERNESS_DESCRIPTION.toString());
        } else {
            Faction faction = factions.get("0");
            if (!faction.getTag().equalsIgnoreCase(TL.WILDERNESS.toString())) {
                faction.setTag(TL.WILDERNESS.toString());
            }
            if (!faction.getDescription().equalsIgnoreCase(TL.WILDERNESS_DESCRIPTION.toString())) {
                faction.setDescription(TL.WILDERNESS_DESCRIPTION.toString());
            }
        }

        // Make sure the safe zone faction exists
        if (!factions.containsKey("-1")) {
            Faction faction = generateFactionObject("-1");
            factions.put("-1", faction);
            faction.setTag(TL.SAFEZONE.toString());
            faction.setDescription(TL.SAFEZONE_DESCRIPTION.toString());
        } else {
            Faction faction = factions.get("-1");
            if (!faction.getTag().equalsIgnoreCase(TL.SAFEZONE.toString())) {
                faction.setTag(TL.SAFEZONE.toString());
            }
            if (!faction.getDescription().equalsIgnoreCase(TL.SAFEZONE_DESCRIPTION.toString())) {
                faction.setDescription(TL.SAFEZONE_DESCRIPTION.toString());
            }
            // if SafeZone has old pre-1.6.0 name, rename it to remove troublesome " "
            if (faction.getTag().contains(" ")) {
                faction.setTag(TL.SAFEZONE.toString());
            }
        }

        // Make sure the war zone faction exists
        if (!factions.containsKey("-2")) {
            Faction faction = generateFactionObject("-2");
            factions.put("-2", faction);
            faction.setTag(TL.WARZONE.toString());
            faction.setDescription(TL.WARZONE_DESCRIPTION.toString());
        } else {
            Faction faction = factions.get("-2");
            if (!faction.getTag().equalsIgnoreCase(TL.WARZONE.toString())) {
                faction.setTag(TL.WARZONE.toString());
            }
            if (!faction.getDescription().equalsIgnoreCase(TL.WARZONE_DESCRIPTION.toString())) {
                faction.setDescription(TL.WARZONE_DESCRIPTION.toString());
            }
            // if WarZone has old pre-1.6.0 name, rename it to remove troublesome " "
            if (faction.getTag().contains(" ")) {
                faction.setTag(TL.WARZONE.toString());
            }
        }
    }

    public Faction getFactionById(String id) {
        return factions.get(id);
    }

    public abstract Faction generateFactionObject(String string);

    public Faction getByTag(String str) {
        String compStr = MiscUtil.getComparisonString(str);
        for (Faction faction : factions.values()) {
            if (faction.getComparisonTag().equals(compStr)) {
                return faction;
            }
        }
        return null;
    }

    public Faction getBestTagMatch(String start) {
        int best = 0;
        start = start.toLowerCase();
        int minlength = start.length();
        Faction bestMatch = null;
        for (Faction faction : factions.values()) {
            String candidate = faction.getTag();
            candidate = ChatColor.stripColor(candidate);
            if (candidate.length() < minlength) {
                continue;
            }
            if (!candidate.toLowerCase().startsWith(start)) {
                continue;
            }

            // The closer to zero the better
            int lendiff = candidate.length() - minlength;
            if (lendiff == 0) {
                return faction;
            }
            if (lendiff < best || best == 0) {
                best = lendiff;
                bestMatch = faction;
            }
        }

        return bestMatch;
    }

    public boolean isTagTaken(String str) {
        return this.getByTag(str) != null;
    }

    public boolean isValidFactionId(String id) {
        return factions.containsKey(id);
    }

    public Faction createFaction() {
        Faction faction = generateFactionObject();
        factions.put(faction.getId(), faction);
        return faction;
    }

    public Set<String> getFactionTags() {
        Set<String> tags = new HashSet<String>();
        for (Faction faction : factions.values()) {
            tags.add(faction.getTag());
        }
        return tags;
    }

    public abstract Faction generateFactionObject();

    public void removeFaction(String id) {
        factions.remove(id).remove();
    }

    @Override
    public ArrayList<Faction> getAllFactions() {
        return new ArrayList<Faction>(factions.values());
    }

    @Override
    public Faction getNone() {
        return factions.get("0");
    }

    @Override
    public Faction getWilderness() {
        return factions.get("0");
    }

    @Override
    public Faction getSafeZone() {
        return factions.get("-1");
    }

    @Override
    public Faction getWarZone() {
        return factions.get("-2");
    }

    public abstract void convertFrom(MemoryFactions old);
}
