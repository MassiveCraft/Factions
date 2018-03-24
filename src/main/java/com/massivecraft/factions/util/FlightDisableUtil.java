package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class FlightDisableUtil extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            FPlayer pilot = FPlayers.getInstance().getByPlayer(player);
            if (pilot.isFlying() && !pilot.isAdminBypassing()) {
                if (enemiesNearby(pilot, 5)) {
                    pilot.msg(TL.COMMAND_FLY_ENEMY_DISABLE);
                    pilot.setFlying(false);
                }
            }
        }
    }

    public static boolean enemiesNearby(FPlayer target, int radius) {
        List<Entity> nearbyEntities = target.getPlayer().getNearbyEntities(radius, radius, radius);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player) {
                FPlayer playerNearby = FPlayers.getInstance().getByPlayer((Player) entity);
                if (playerNearby.isAdminBypassing()) {
                    continue;
                }
                if (playerNearby.getRelationTo(target) == Relation.ENEMY) {
                    return true;
                }
            }
        }
        return false;
    }

}
