package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a Faction is created.
 */
public class FactionCreateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private String factionTag;
    private Player sender;
    private boolean cancelled;

    public FactionCreateEvent(Player sender, String tag) {
        this.factionTag = tag;
        this.sender = sender;
        this.cancelled = false;
    }

    public FPlayer getFPlayer() {
        return FPlayers.getInstance().getByPlayer(sender);
    }

    public String getFactionTag() {
        return factionTag;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean c) {
        this.cancelled = c;
    }
}