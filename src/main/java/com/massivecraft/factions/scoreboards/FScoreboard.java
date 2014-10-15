package com.massivecraft.factions.scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Lazy attempt at abstraction off of 40 minutes of sleep.
 */
public abstract class FScoreboard {

    public Objective objective;
    public Scoreboard scoreboard;

    public void apply(Player player) {
        player.setScoreboard(scoreboard);
    }

    public void remove(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
