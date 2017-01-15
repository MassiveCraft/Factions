package com.massivecraft.factions.scoreboards;

import com.massivecraft.factions.*;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class FTeamWrapper {
    private static final Map<Faction, FTeamWrapper> wrappers = new HashMap<Faction, FTeamWrapper>();
    private static final List<FScoreboard> tracking = new ArrayList<FScoreboard>();
    private static int factionTeamPtr;
    private static final Set<Faction> updating = new HashSet<Faction>();

    private final Map<FScoreboard, Team> teams = new HashMap<FScoreboard, Team>();
    private final String teamName;
    private final Faction faction;
    private final Set<OfflinePlayer> members = new HashSet<OfflinePlayer>();

    public static void applyUpdatesLater(final Faction faction) {
        if (!FScoreboard.isSupportedByServer()) {
            return;
        }

        if (faction.isWilderness()) {
            return;
        }

        if (!P.p.getConfig().getBoolean("scoreboard.default-prefixes", false)) {
            return;
        }


        if (updating.add(faction)) {
            Bukkit.getScheduler().runTask(P.p, new Runnable() {
                @Override
                public void run() {
                    updating.remove(faction);
                    applyUpdates(faction);
                }
            });
        }
    }

    public static void applyUpdates(Faction faction) {
        if (!FScoreboard.isSupportedByServer()) {
            return;
        }

        if (faction.isWilderness()) {
            return;
        }

        if (!P.p.getConfig().getBoolean("scoreboard.default-prefixes", false)) {
            return;
        }

        if (updating.contains(faction)) {
            // Faction will be updated soon.
            return;
        }

        FTeamWrapper wrapper = wrappers.get(faction);
        Set<FPlayer> factionMembers = faction.getFPlayers();

        if (wrapper != null && Factions.getInstance().getFactionById(faction.getId()) == null) {
            // Faction was disbanded
            wrapper.unregister();
            wrappers.remove(faction);
            return;
        }

        if (wrapper == null) {
            wrapper = new FTeamWrapper(faction);
            wrappers.put(faction, wrapper);
        }

        for (OfflinePlayer player : wrapper.getPlayers()) {
            if (!player.isOnline() || !factionMembers.contains(FPlayers.getInstance().getByOfflinePlayer(player))) {
                // Player is offline or no longer in faction
                wrapper.removePlayer(player);
            }
        }

        for (FPlayer fmember : factionMembers) {
            if (!fmember.isOnline()) {
                continue;
            }

            // Scoreboard might not have player; add him/her
            wrapper.addPlayer(fmember.getPlayer());
        }

        wrapper.updatePrefixes();
    }

    public static void updatePrefixes(Faction faction) {
        if (!FScoreboard.isSupportedByServer()) {
            return;
        }

        if (!wrappers.containsKey(faction)) {
            applyUpdates(faction);
        } else {
            wrappers.get(faction).updatePrefixes();
        }
    }

    protected static void track(FScoreboard fboard) {
        if (!FScoreboard.isSupportedByServer()) {
            return;
        }
        tracking.add(fboard);
        for (FTeamWrapper wrapper : wrappers.values()) {
            wrapper.add(fboard);
        }
    }

    protected static void untrack(FScoreboard fboard) {
        if (!FScoreboard.isSupportedByServer()) {
            return;
        }
        tracking.remove(fboard);
        for (FTeamWrapper wrapper : wrappers.values()) {
            wrapper.remove(fboard);
        }
    }


    private FTeamWrapper(Faction faction) {
        this.teamName = "faction_" + (factionTeamPtr++);
        this.faction = faction;

        for (FScoreboard fboard : tracking) {
            add(fboard);
        }
    }

    private void add(FScoreboard fboard) {
        Scoreboard board = fboard.getScoreboard();
        Team team = board.registerNewTeam(teamName);
        teams.put(fboard, team);

        for (OfflinePlayer player : getPlayers()) {
            team.addPlayer(player);
        }

        updatePrefix(fboard);
    }

    private void remove(FScoreboard fboard) {
        teams.remove(fboard).unregister();
    }

    private void updatePrefixes() {
        if (P.p.getConfig().getBoolean("scoreboard.default-prefixes", false)) {
            for (FScoreboard fboard : teams.keySet()) {
                updatePrefix(fboard);
            }
        }
    }

    private void updatePrefix(FScoreboard fboard) {
        if (P.p.getConfig().getBoolean("scoreboard.default-prefixes", false)) {
            FPlayer fplayer = fboard.getFPlayer();
            Team team = teams.get(fboard);

            String prefix = TL.DEFAULT_PREFIX.toString();
            prefix = prefix.replace("{relationcolor}", faction.getRelationTo(fplayer).getColor().toString());
            prefix = prefix.replace("{faction}", faction.getTag().substring(0, Math.min("{faction}".length() + 16 - prefix.length(), faction.getTag().length())));
            if (team.getPrefix() == null || !team.getPrefix().equals(prefix)) {
                team.setPrefix(prefix);
            }
        }
    }

    private void addPlayer(OfflinePlayer player) {
        if (members.add(player)) {
            for (Team team : teams.values()) {
                team.addPlayer(player);
            }
        }
    }

    private void removePlayer(OfflinePlayer player) {
        if (members.remove(player)) {
            for (Team team : teams.values()) {
                team.removePlayer(player);
            }
        }
    }

    private Set<OfflinePlayer> getPlayers() {
        return new HashSet<OfflinePlayer>(this.members);
    }

    private void unregister() {
        for (Team team : teams.values()) {
            team.unregister();
        }
        teams.clear();
    }
}

