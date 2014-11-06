package com.massivecraft.factions.zcore.persist.json;

import com.massivecraft.factions.*;
import com.massivecraft.factions.zcore.persist.MemoryBoard;
import com.massivecraft.factions.zcore.persist.MemoryFPlayers;
import com.massivecraft.factions.zcore.persist.MemoryFactions;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;

public class FactionsJSON {

    public static void convertTo() {
        if (!(Factions.getInstance() instanceof MemoryFactions)) {
            return;
        }
        if (!(FPlayers.getInstance() instanceof MemoryFPlayers)) {
            return;
        }
        if (!(Board.getInstance() instanceof MemoryBoard)) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                Logger logger = P.p.getLogger();
                logger.info("Beginning Board conversion to JSON");
                new JSONBoard().convertFrom((MemoryBoard) Board.getInstance());
                logger.info("Board Converted");
                logger.info("Beginning FPlayers conversion to JSON");
                new JSONFPlayers().convertFrom((MemoryFPlayers) FPlayers.getInstance());
                logger.info("FPlayers Converted");
                logger.info("Beginning Factions conversion to JSON");
                new JSONFactions().convertFrom((MemoryFactions) Factions.getInstance());
                logger.info("Factions Converted");
                logger.info("Refreshing object caches");
                for (FPlayer fPlayer : FPlayers.getInstance().getAllFPlayers()) {
                    Faction faction = Factions.getInstance().getFactionById(fPlayer.getFactionId());
                    faction.addFPlayer(fPlayer);
                }
                logger.info("Conversion Complete");
            }
        }.runTaskAsynchronously(P.p);
    }
}
