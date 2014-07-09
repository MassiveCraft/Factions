package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FactionRenameEvent extends FactionPlayerEvent implements Cancellable {

    private boolean cancelled = false;
    private String tag;

    public FactionRenameEvent(FPlayer sender, String newTag) {
        super(sender.getFaction(), sender);
        tag = newTag;
    }

    @Deprecated
    public Player getPlayer() {
        return getfPlayer().getPlayer();
    }

    @Deprecated
    public String getOldFactionTag() {
        return getFaction().getTag();
    }

    public String getFactionTag() {
        return tag;
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
