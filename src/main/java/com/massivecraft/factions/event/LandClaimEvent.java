package com.massivecraft.factions.event;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LandClaimEvent extends FactionPlayerEvent implements Cancellable {

    private boolean cancelled;
    private FLocation location;

    public LandClaimEvent(FLocation loc, Faction f, FPlayer p) {
        super(f, p);
        cancelled = false;
        location = loc;
    }

    public FLocation getLocation() {
        return this.location;
    }

    @Deprecated
    public String getFactionId() {
        return getFaction().getId();
    }

    @Deprecated
    public String getFactionTag() {
        return getFaction().getTag();
    }

    @Deprecated
    public Player getPlayer() {
        return getfPlayer().getPlayer();
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
