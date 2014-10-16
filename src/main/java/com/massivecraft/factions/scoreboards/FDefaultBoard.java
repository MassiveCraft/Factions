package com.massivecraft.factions.scoreboards;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.scoreboards.tasks.UpdateTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;
import java.util.logging.Level;

public class FDefaultBoard implements FScoreboard {

    private FPlayer fPlayer;
    private Scoreboard scoreboard;
    private Objective objective;
    private int taskId;

    public FDefaultBoard(FPlayer player) {
        this.fPlayer = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        setup();
        apply(fPlayer.getPlayer());
    }

    public void setup() {
        objective = scoreboard.registerNewObjective("default", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.taskId = Bukkit.getScheduler().runTaskTimer(P.p, new UpdateTask(this), 40L, P.p.getConfig().getLong("default-update-interval", 20L)).getTaskId();
        update(objective);
    }

    public void apply(Player player) {
        player.setScoreboard(scoreboard);
    }

    public void remove(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public void update(Objective buffer) {
        if(fPlayer.getPlayer() == null || !fPlayer.getPlayer().isOnline()) {
            Bukkit.getScheduler().cancelTask(taskId);
            return;
        }
        buffer.setDisplayName(ChatColor.translateAlternateColorCodes('&', P.p.getConfig().getString("scoreboard.default-title", "i love drt")));

        List<String> list = P.p.getConfig().getStringList("scoreboard.default");
        int place = 16; // list.size();

        if (list == null) {
            P.p.debug(Level.WARNING, "scoreboard.default is null :(");
            return;
        }

        for (String s : list) {
            String replaced = replace(s);
            String awesome = replaced.length() > 16 ? replaced.substring(0, 15) : replaced;
            Score score = buffer.getScore(awesome);
            score.setScore(place);

            place--;
            if (place < 0) {
                break; // Let's not let the scoreboard get too big.
            }
        }
        buffer.setDisplaySlot(DisplaySlot.SIDEBAR);
        if(!buffer.getName().equalsIgnoreCase("default")) {
            objective.unregister(); // unregister so we don't have to worry about duplicate names.
            this.objective = buffer;
        }
    }

    private String replace(String s) {
        String faction = !fPlayer.getFaction().isNone() ? fPlayer.getFaction().getTag() : "factionless";
        s = s.replace("{name}", fPlayer.getName())
                .replace("{power}", String.valueOf(fPlayer.getPowerRounded()))
                .replace("{balance}", String.valueOf(Econ.getFriendlyBalance(fPlayer.getPlayer().getUniqueId())))
                .replace("{faction}", faction)
                .replace("{maxPower}", String.valueOf(fPlayer.getPowerMaxRounded()))
                .replace("{totalOnline}", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
}
