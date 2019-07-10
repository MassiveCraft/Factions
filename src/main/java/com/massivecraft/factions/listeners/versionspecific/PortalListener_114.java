package com.massivecraft.factions.listeners.versionspecific;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;

/*
  Blocking all portal creation not in wilderness because we can't properly check if the creator has permission
  to create at the target destination.
 */
public class PortalListener_114 implements Listener {

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

        // Only 8 blocks so a loop should be fine.
        for (BlockState block : event.getBlocks()) {
            FLocation loc = new FLocation(block.getLocation());
            Faction faction = Board.getInstance().getFactionAt(loc);

            if (faction.isWilderness()) {
                continue; // We don't care about wilderness.
            } else if (!faction.isNormal() && !player.isOp()) {
                // Don't let non ops make portals in safezone or warzone.
                event.setCancelled(true);
                return;
            }

            String mininumRelation = P.p.getConfig().getString("portals.minimum-relation", "MEMBER");

            // Don't let people portal into nether bases if server owners don't want that.
            if (!fPlayer.getFaction().getRelationTo(faction).isAtLeast(Relation.fromString(mininumRelation))) {
                event.setCancelled(true);
                player.sendMessage(TL.PLAYER_PORTAL_NOTALLOWED.toString());
                return;
            }
        }

    }
}
