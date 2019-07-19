package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.scheduler.BukkitRunnable;

public class WarmUpUtil {

    /**
     * @param player         The player to notify.
     * @param translationKey The translation key used for notifying.
     * @param action         The action, inserted into the notification message.
     * @param runnable       The task to run after the delay. If the delay is 0, the task is instantly ran.
     * @param delay          The time used, in seconds, for the delay.
     *                       <p/>
     *                       note: for translations: %s = action, %d = delay
     */
    public static void process(final FPlayer player, Warmup warmup, TL translationKey, String action, final Runnable runnable, long delay) {
        if (delay > 0) {
            if (player.isWarmingUp()) {
                player.msg(TL.WARMUPS_ALREADY);
            } else {
                player.msg(translationKey.format(action, delay));
                int id = new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.stopWarmup();
                        runnable.run();
                    }
                }.runTaskLater(P.p, delay * 20).getTaskId();
                player.addWarmup(warmup, id);
            }
        } else {
            runnable.run();
        }
    }

    public enum Warmup {
        HOME, WARP, FLIGHT
    }

}
