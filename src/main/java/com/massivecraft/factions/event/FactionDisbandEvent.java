package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FactionDisbandEvent extends FactionEvent implements Cancellable {

    private boolean cancelled = false;
    private Player sender;

    public FactionDisbandEvent(Player sender, String factionId) {
        super(Factions.i.get(factionId));
        this.sender = sender;
    }

    public FPlayer getFPlayer() {
        return FPlayers.i.get(sender);
    }

    public Player getPlayer() {
        return sender;
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
