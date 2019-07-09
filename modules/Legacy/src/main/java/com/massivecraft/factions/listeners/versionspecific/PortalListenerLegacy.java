package com.massivecraft.factions.listeners.versionspecific;

import org.bukkit.TravelAgent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

/*
   Supports versions older than 1.14 with TravelAgent.
 */
public class PortalListenerLegacy implements Listener {
    private PortalListenerBase base;

    public PortalListenerLegacy(PortalListenerBase base) {
        this.base = base;
    }

    @EventHandler
    public void onTravel(PlayerPortalEvent event) {
        TravelAgent agent = event.getPortalTravelAgent();

        // If they aren't able to find a portal, it'll try to create one.
        if (event.useTravelAgent() && agent.getCanCreatePortal() && agent.findPortal(event.getTo()) == null) {
            if (this.base.shouldCancel(event.getTo(), event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }
}
