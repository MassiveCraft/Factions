package com.massivecraft.factions.scoreboards;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class FScoreboard {
    private static Map<Player, FScoreboard> fscoreboards = new HashMap<Player, FScoreboard>();

    private final Scoreboard scoreboard;
    private final FPlayer fplayer;
    private final BufferedObjective bufferedObjective;
    private final Map<ChatColor, Team> colorTeams = new HashMap<ChatColor, Team>();
    private FSidebarProvider defaultProvider;
    private FSidebarProvider temporaryProvider;
    private boolean removed = false;

    public static void init(FPlayer fplayer) {
        fscoreboards.put(fplayer.getPlayer(), new FScoreboard(fplayer));

        updateColorToAllLater(fplayer);
        updateColorsFromAllLater(fplayer);
    }

    public static void remove(FPlayer fplayer) {
        fscoreboards.remove(fplayer.getPlayer()).removed = true;
    }

    public static FScoreboard get(FPlayer player) {
        return get(player.getPlayer());
    }

    public static FScoreboard get(Player player) {
        return fscoreboards.get(player);
    }

    public static void updateColorToAllLater(final FPlayer fplayer) {
        // We're delaying by a tick here to simplify logic in other areas
        // (e.g. for FPlayer{Join,Leave}Event handlers; CmdDisband)
        Bukkit.getScheduler().runTask(P.p, new Runnable() {
            @Override
            public void run() {
                for (FPlayer other : FPlayers.i.getOnline()) {
                    get(other).updateColor(fplayer);
                }
            }
        });
    }

    public static void updateColorsFromAllLater(final FPlayer fplayer) {
        Bukkit.getScheduler().runTask(P.p, new Runnable() {
            @Override
            public void run() {
                for (FPlayer other : FPlayers.i.getOnline()) {
                    get(fplayer).updateColor(other);
                }
            }
        });
    }

    private FScoreboard(FPlayer fplayer) {
        this.fplayer = fplayer;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.bufferedObjective = new BufferedObjective(scoreboard);

        for (ChatColor color : ChatColor.values()) {
            Team team = scoreboard.registerNewTeam(color.name());
            team.setPrefix(color.toString());
            colorTeams.put(color, team);
        }

        fplayer.getPlayer().setScoreboard(scoreboard);
    }

    public void setSidebarVisibility(boolean visible) {
        bufferedObjective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
    }

    public void setDefaultSidebar(final FSidebarProvider provider, int updateInterval) {
        defaultProvider = provider;
        if (temporaryProvider == null) {
            // We have no temporary provider; update the BufferedObjective!
            updateObjective();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (removed || provider != defaultProvider) {
                    cancel();
                    return;
                }

                if (temporaryProvider == null) {
                    updateObjective();
                }
            }
        }.runTaskTimer(P.p, updateInterval, updateInterval);
    }

    public void setTemporarySidebar(final FSidebarProvider provider) {
        temporaryProvider = provider;
        updateObjective();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (removed) {
                    return;
                }

                if (temporaryProvider == provider) {
                    temporaryProvider = null;
                    updateObjective();
                }
            }
        }.runTaskLater(P.p, P.p.getConfig().getInt("scoreboard.expiration", 7) * 20);
    }

    private void updateObjective() {
        FSidebarProvider provider = temporaryProvider != null ? temporaryProvider : defaultProvider;

        if (provider == null) {
            bufferedObjective.hide();
        } else {
            bufferedObjective.setTitle(provider.getTitle(fplayer));
            bufferedObjective.setAllLines(provider.getLines(fplayer));
            bufferedObjective.flip();
        }
    }

    public void updateColor(FPlayer other) {
        if (!other.isOnline()) {
            return;
        }

        ChatColor newColor = fplayer.getRelationTo(other).getColor();
        Team team = colorTeams.get(newColor);

        if (!team.hasPlayer(other.getPlayer())) {
            team.addPlayer(other.getPlayer());
        }
    }
}
