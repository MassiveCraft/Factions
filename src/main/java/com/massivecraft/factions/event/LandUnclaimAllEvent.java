package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;

public class LandUnclaimAllEvent extends FactionPlayerEvent {

    public LandUnclaimAllEvent(Faction f, FPlayer p) {
        super(f, p);
    }

    /**
     * Get the id of the faction.
     *
     * @return id of faction as String
     *
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
     *
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
     *
     * @deprecated use getfPlayer().getPlayer() instead.
     */
    @Deprecated
    public Player getPlayer() {
        return getfPlayer().getPlayer();
    }
}
