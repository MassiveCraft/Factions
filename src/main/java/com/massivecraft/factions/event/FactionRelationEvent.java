package com.massivecraft.factions.event;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a Faction relation is called.
 */
public class FactionRelationEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Faction fsender;
    private Faction ftarget;
    private Relation foldrel;
    private Relation frel;

    public FactionRelationEvent(Faction sender, Faction target, Relation oldrel, Relation rel) {
        fsender = sender;
        ftarget = target;
        foldrel = oldrel;
        frel = rel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Relation getOldRelation() {
        return foldrel;
    }

    public Relation getRelation() {
        return frel;
    }

    public Faction getFaction() {
        return fsender;
    }

    public Faction getTargetFaction() {
        return ftarget;
    }
}
