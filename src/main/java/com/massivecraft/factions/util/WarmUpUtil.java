package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.util.TL;

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
    public static void process(FPlayer player, TL translationKey, String action, Runnable runnable, long delay) {
        if (delay > 0) {
            player.msg(translationKey.format(action, delay));
            P.p.getServer().getScheduler().runTaskLater(P.p, runnable, delay * 20);
        } else {
            runnable.run();
        }
    }
}
