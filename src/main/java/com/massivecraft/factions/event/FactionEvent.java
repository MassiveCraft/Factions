package com.massivecraft.factions.event;

import com.massivecraft.factions.Faction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents an event involving a Faction.
 */
public class FactionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Faction faction;

    public FactionEvent(Faction faction) {
        this.faction = faction;
    }

    /**
     * Get the Faction involved in the event.
     *
     * @return faction involved in the event.
     */
    public Faction getFaction() {
        return this.faction;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
