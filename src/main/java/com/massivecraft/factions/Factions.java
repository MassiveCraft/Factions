package com.massivecraft.factions;

import java.util.ArrayList;
import java.util.Set;

import com.massivecraft.factions.zcore.persist.json.JSONFactions;

public abstract class Factions {
    protected static Factions instance = getFactionsImpl();

    public abstract Faction getFactionById(String id);

    public abstract Faction getByTag(String str);

    public abstract Faction getBestTagMatch(String start);

    public abstract boolean isTagTaken(String str);

    public abstract boolean isValidFactionId(String id);

    public abstract Faction createFaction();

    public abstract void removeFaction(String id);

    public abstract Set<String> getFactionTags();

    public abstract ArrayList<Faction> getAllFactions();

    public abstract Faction getNone();

    public abstract Faction getSafeZone();

    public abstract Faction getWarZone();

    public abstract void forceSave();

    public static Factions getInstance() {
        return instance;
    }

    private static Factions getFactionsImpl() {
        switch (Conf.backEnd) {
            case JSON:
                return new JSONFactions();
        }
        return null;
    }

    public abstract void load();
}
