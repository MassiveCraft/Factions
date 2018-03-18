package com.massivecraft.factions.listeners;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.logging.Level;

public class EssentialsListener implements Listener {

    private final IEssentials ess;

    public EssentialsListener(IEssentials essentials) {
        this.ess = essentials;
    }

    @EventHandler
    public void onLeave(FPlayerLeaveEvent event) throws Exception {
        // Get the USER from their UUID.
        Faction faction = event.getFaction();
        User user = ess.getUser(UUID.fromString(event.getfPlayer().getId()));

        // Not a great way to do this on essential's side.
        for (String homeName : user.getHomes()) {

            // This can throw an exception for some reason.
            Location loc = user.getHome(homeName);
            FLocation floc = new FLocation(loc);

            // We're only going to remove homes in territory that belongs to THEIR faction.
            if (Board.getInstance().getFactionAt(floc).equals(faction)) {
                user.delHome(homeName);
                P.p.log(Level.INFO, "FactionLeaveEvent: Removing home %s, player %s, in territory of %s",
                        homeName, event.getfPlayer().getName(), faction.getTag());
            }
        }
    }
}
