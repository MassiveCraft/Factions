package com.massivecraft.factions;

import com.massivecraft.factions.zcore.persist.json.JSONBoard;

import java.util.ArrayList;


public abstract class Board {
    protected static Board instance = getBoardImpl();

    //----------------------------------------------//
    // Get and Set
    //----------------------------------------------//
    public abstract String getIdAt(FLocation flocation);

    private static Board getBoardImpl() {
        switch (Conf.backEnd) {
            case JSON:
                return new JSONBoard();
        }
        return null;
    }

    public static Board getInstance() {
        return instance;
    }

    public abstract Faction getFactionAt(FLocation flocation);

    public abstract void setIdAt(String id, FLocation flocation);

    public abstract void setFactionAt(Faction faction, FLocation flocation);

    public abstract void removeAt(FLocation flocation);

    // not to be confused with claims, ownership referring to further member-specific ownership of a claim
    public abstract void clearOwnershipAt(FLocation flocation);

    public abstract void unclaimAll(String factionId);

    // Is this coord NOT completely surrounded by coords claimed by the same faction?
    // Simpler: Is there any nearby coord with a faction other than the faction here?
    public abstract boolean isBorderLocation(FLocation flocation);

    // Is this coord connected to any coord claimed by the specified faction?
    public abstract boolean isConnectedLocation(FLocation flocation, Faction faction);

    // Is this location outside the world border? 
    public abstract boolean isOutsideWorldBorder(FLocation flocation, int buffer);
    
    public abstract boolean hasFactionWithin(FLocation flocation, Faction faction, int radius);

    //----------------------------------------------//
    // Cleaner. Remove orphaned foreign keys
    //----------------------------------------------//

    public abstract void clean();

    //----------------------------------------------//
    // Coord count
    //----------------------------------------------//

    public abstract int getFactionCoordCount(String factionId);

    public abstract int getFactionCoordCount(Faction faction);

    public abstract int getFactionCoordCountInWorld(Faction faction, String worldName);

    //----------------------------------------------//
    // Map generation
    //----------------------------------------------//

    /**
     * The map is relative to a coord and a faction north is in the direction of decreasing x east is in the direction
     * of decreasing z
     */
    public abstract ArrayList<String> getMap(Faction faction, FLocation flocation, double inDegrees);

    public abstract boolean forceSave();

    public abstract boolean load();
}
