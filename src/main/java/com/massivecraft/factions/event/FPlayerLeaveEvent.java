package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.event.Cancellable;

public class FPlayerLeaveEvent extends FactionPlayerEvent implements Cancellable {

    private PlayerLeaveReason reason;
    boolean cancelled = false;

    public enum PlayerLeaveReason {
        KICKED, DISBAND, RESET, JOINOTHER, LEAVE, BANNED
    }

    public FPlayerLeaveEvent(FPlayer p, Faction f, PlayerLeaveReason r) {
        super(f, p);
        reason = r;
    }

    /**
     * Get the reason the player left the faction.
     *
     * @return reason player left the faction.
     */
    public PlayerLeaveReason getReason() {
        return reason;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean c) {
        // Don't let them cancel factions disbanding.
        cancelled = reason != PlayerLeaveReason.DISBAND && reason != PlayerLeaveReason.RESET && c;
    }
}
