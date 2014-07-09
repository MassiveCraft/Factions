package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FPlayerLeaveEvent extends FactionPlayerEvent implements Cancellable {

    private PlayerLeaveReason reason;
    boolean cancelled = false;

    public enum PlayerLeaveReason {
        KICKED, DISBAND, RESET, JOINOTHER, LEAVE
    }

    public FPlayerLeaveEvent(FPlayer p, Faction f, PlayerLeaveReason r) {
        super(f, p);
        reason = r;
    }

    public PlayerLeaveReason getReason() {
        return reason;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean c) {
        if (reason == PlayerLeaveReason.DISBAND || reason == PlayerLeaveReason.RESET) {
            cancelled = false; // Don't let them cancel factions disbanding.
        } else {
        cancelled = c;
        }
    }
}