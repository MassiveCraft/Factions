package com.massivecraft.factions.scoreboards;

import com.massivecraft.factions.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FScoreboard {
    private static Map<FPlayer, FScoreboard> fscoreboards = new HashMap<FPlayer, FScoreboard>();

    private final Scoreboard scoreboard;
    private final FPlayer fplayer;
    private final BufferedObjective bufferedObjective;
    private final Map<Faction, Team> factionTeams = new HashMap<Faction, Team>();
    private int factionTeamPtr;
    private FSidebarProvider defaultProvider;
    private FSidebarProvider temporaryProvider;
    private boolean removed = false;

    public static void init(FPlayer fplayer) {
        fscoreboards.put(fplayer, new FScoreboard(fplayer));
        if (fplayer.hasFaction()) {
            FScoreboard.applyUpdates(fplayer.getFaction());
        }
    }

    public static void remove(FPlayer fplayer) {
        fscoreboards.remove(fplayer).removed = true;
    }

    public static FScoreboard get(FPlayer fplayer) {
        return fscoreboards.get(fplayer);
    }

    public static FScoreboard get(Player player) {
        return fscoreboards.get(FPlayers.i.get(player));
    }

    public static void applyUpdatesLater(final Faction faction) {
        Bukkit.getScheduler().runTask(P.p, new Runnable() {
            @Override
            public void run() {
                applyUpdates(faction);
            }
        });
    }

    public static void applyUpdates(Faction faction) {
        for (FScoreboard fscoreboard : fscoreboards.values()) {
            fscoreboard.updateFactionTeam(faction);
        }
    }

    private FScoreboard(FPlayer fplayer) {
        this.fplayer = fplayer;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.bufferedObjective = new BufferedObjective(scoreboard);

        for (Faction faction : Factions.i.get()) {
            updateFactionTeam(faction);
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

    public void updateFactionTeam(Faction faction) {
        Team team = factionTeams.get(faction);
        Set<FPlayer> factionMembers = faction.getFPlayers();

        if (!Factions.i.get().contains(faction)) {
            // Faction was disbanded
            if (team != null) {
                factionTeams.remove(faction);
                team.unregister();
            }
            return;
        }

        if (team == null) {
            team = scoreboard.registerNewTeam("faction_" + (factionTeamPtr++));
            factionTeams.put(faction, team);
        }

        for (OfflinePlayer player : team.getPlayers()) {
            if (!player.isOnline() || !factionMembers.contains(FPlayers.i.get(player.getPlayer()))) {
                // Player is offline or no longer in faction
                team.removePlayer(player);
            }
        }

        for (FPlayer fmember : factionMembers) {
            if (!fmember.isOnline()) {
                continue;
            }
            if (!team.hasPlayer(fmember.getPlayer())) {
                // Scoreboard team doesn't have player; add him/her
                team.addPlayer(fmember.getPlayer());
            }
        }

        // Update faction prefix
        String prefix = faction.getRelationTo(this.fplayer).getColor() + "[" + faction.getTag().substring(0, Math.min(9, faction.getTag().length())) + "] " + ChatColor.RESET;
        if (team.getPrefix() == null || !team.getPrefix().equals(prefix)) {
            team.setPrefix(prefix);
        }
    }
}
