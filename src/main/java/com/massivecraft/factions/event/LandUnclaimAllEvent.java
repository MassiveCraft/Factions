package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LandUnclaimAllEvent extends FactionPlayerEvent {

    public LandUnclaimAllEvent(Faction f, FPlayer p) {
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
}
