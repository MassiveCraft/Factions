package com.massivecraft.factions.scoreboards.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Class that can be used to simply reset a Player's scoreboard in the future.
 */
public class ExpirationTask extends BukkitRunnable {

    private String name;
    private Scoreboard board;

    public ExpirationTask(String name, Scoreboard scoreboard) {
        this.board = scoreboard;
        this.name = name;
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            return;
        }

        if (player.getScoreboard().equals(board)) { // Incase someone else changed the board.
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }
}
