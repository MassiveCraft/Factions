package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PowerLossEvent extends FactionPlayerEvent implements Cancellable {

    private boolean cancelled = false;
    private String message;

    public PowerLossEvent(Faction f, FPlayer p) {
        super(f, p);
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
