package com.massivecraft.factions.listeners.versionspecific;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.List;

/*
  Blocking all portal creation not in wilderness because we can't properly check if the creator has permission
  to create at the target destination.
 */
public class PortalListener_114 implements Listener {

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        List<Block> blocks = event.getBlocks();
        for (Block block : blocks) {
            FLocation loc = new FLocation(block.getLocation());
            Faction faction = Board.getInstance().getFactionAt(loc);
            if (!faction.isWilderness()) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
