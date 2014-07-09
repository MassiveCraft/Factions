package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FPlayerJoinEvent extends FactionPlayerEvent implements Cancellable {

    PlayerJoinReason reason;
    boolean cancelled = false;

    public enum PlayerJoinReason {
        CREATE, LEADER, COMMAND
    }

    public FPlayerJoinEvent(FPlayer fp, Faction f, PlayerJoinReason r) {
        super(f, fp);
        reason = r;
    }

    public PlayerJoinReason getReason() {
        return reason;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean c) {
        cancelled = c;
    }
}