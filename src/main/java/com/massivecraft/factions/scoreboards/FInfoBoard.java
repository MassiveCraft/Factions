package com.massivecraft.factions.scoreboards;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.scoreboards.tasks.ExpirationTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;

import java.util.List;
import java.util.logging.Level;

public class FInfoBoard extends FScoreboard {

    private Faction faction;

    public FInfoBoard(Player player, Faction faction, boolean timed) {
        this.faction = faction;
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        setup(player);
        apply(player);

        if (timed) {
            new ExpirationTask(player.getName(), scoreboard).runTaskLater(P.p, P.p.getConfig().getInt("scoreboard.expiration", 7) * 20L); // remove after 10 seconds.
        }
    }

    private void setup(Player player) {
        FPlayer fPlayer = FPlayers.i.get(player);
        objective = scoreboard.registerNewObjective("FBoard", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(faction.getRelationTo(fPlayer).getColor() + faction.getTag());

        List<String> list = P.p.getConfig().getStringList("scoreboard.finfo");
        int place = 16; // list.size();

        if (list == null) {
            P.p.debug(Level.WARNING, "scoreboard.finfo is null :(");
            return;
        }

        for (String s : list) {
            String replaced = replace(s);
            String awesome = replaced.length() > 16 ? replaced.substring(0, 15) : replaced;
            Score score = objective.getScore(awesome);
            score.setScore(place);

            place--;
            if (place < 0) {
                break; // Let's not let the scoreboard get too big.
            }
        }
    }

    /**
     * Filters lots of things in accordance with le config.
     *
     * @param s String to replace.
     *
     * @return new String with values instead of placeholders.
     */
    private String replace(String s) {
        boolean raidable = faction.getLandRounded() > faction.getPower();
        FPlayer fLeader = faction.getFPlayerAdmin();
        String leader = fLeader == null ? "Server" : fLeader.getName().substring(0, fLeader.getName().length() > 14 ? 13 : fLeader.getName().length());
        return ChatColor.translateAlternateColorCodes('&', s.replace("{power}", String.valueOf(faction.getPowerRounded())).replace("{online}", String.valueOf(faction.getOnlinePlayers().size())).replace("{members}", String.valueOf(faction.getFPlayers().size())).replace("{leader}", leader).replace("{chunks}", String.valueOf(faction.getLandRounded())).replace("{raidable}", String.valueOf(raidable)));
    }

}
