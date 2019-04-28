package com.massivecraft.factions.listeners.versionspecific;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Relation;
import org.bukkit.TravelAgent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

/*
   Supports versions older than 1.14 with TravelAgent.
 */
public class PortalListenerLegacy implements Listener {

    @EventHandler
    public void onTravel(PlayerPortalEvent event) {
        if (!P.p.getConfig().getBoolean("portals.limit", false)) {
            return; // Don't do anything if they don't want us to.
        }

        TravelAgent agent = event.getPortalTravelAgent();

        // If they aren't able to find a portal, it'll try to create one.
        if (event.useTravelAgent() && agent.getCanCreatePortal() && agent.findPortal(event.getTo()) == null) {
            FLocation loc = new FLocation(event.getTo());
            Faction faction = Board.getInstance().getFactionAt(loc);
            if (faction.isWilderness()) {
                return; // We don't care about wilderness.
            } else if (!faction.isNormal() && !event.getPlayer().isOp()) {
                // Don't let non ops make portals in safezone or warzone.
                event.setCancelled(true);
                return;
            }

            FPlayer fp = FPlayers.getInstance().getByPlayer(event.getPlayer());
            String mininumRelation = P.p.getConfig().getString("portals.minimum-relation", "MEMBER"); // Defaults to Neutral if typed wrong.
            if (!fp.getFaction().getRelationTo(faction).isAtLeast(Relation.fromString(mininumRelation))) {
                event.setCancelled(true);
            }
        }
    }
}
