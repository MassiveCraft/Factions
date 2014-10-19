package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Event called when a faction is disbanded.
 */
public class FactionDisbandEvent extends FactionEvent implements Cancellable {

    private boolean cancelled = false;
    private Player sender;

    public FactionDisbandEvent(Player sender, String factionId) {
        super(Factions.getInstance().getFactionById(factionId));
        this.sender = sender;
    }

    public FPlayer getFPlayer() {
        return FPlayers.getInstance().getByPlayer(sender);
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
