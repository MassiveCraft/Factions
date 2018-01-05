package com.massivecraft.factions.event;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Event called when an FPlayer unclaims land for a Faction.
 */
public class LandUnclaimEvent extends FactionPlayerEvent implements Cancellable {

    private boolean cancelled;
    private FLocation location;

    public LandUnclaimEvent(FLocation loc, Faction f, FPlayer p) {
        super(f, p);
        cancelled = false;
        location = loc;
    }

    public FLocation getLocation() {
        return this.location;
    }

    /**
     * Get the id of the faction.
     *
     * @return id of faction as String
     * @deprecated use getFaction().getId() instead.
     */
    @Deprecated
    public String getFactionId() {
        return getFaction().getId();
    }

    /**
     * Get the tag of the faction.
     *
     * @return tag of faction as String
     * @deprecated use getFaction().getTag() instead.
     */
    @Deprecated
    public String getFactionTag() {
        return getFaction().getTag();
    }

    /**
     * Get the Player involved in the event.
     *
     * @return Player from FPlayer.
     * @deprecated use getfPlayer().getPlayer() instead.
     */
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
        cancelled = c;
    }
}
