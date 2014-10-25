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

    private final Map<FScoreboard, Team> teams = new HashMap<FScoreboard, Team>();
    private final String teamName;
    private final Faction faction;
    private final Set<UUID> members = new HashSet<UUID>();

    public static void applyUpdatesLater(final Faction faction) {
        if (!FScoreboard.isSupportedByServer()) {
            return;
        }

        Bukkit.getScheduler().runTask(P.p, new Runnable() {
            @Override
            public void run() {
                applyUpdates(faction);
            }
        });
    }

    public static void applyUpdates(Faction faction) {
        if (!FScoreboard.isSupportedByServer()) {
            return;
        }

        FTeamWrapper wrapper = wrappers.get(faction);
        Set<FPlayer> factionMembers = faction.getFPlayers();

        if (wrapper != null && !Factions.i.get().contains(faction)) {
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
            if (!player.isOnline() || !factionMembers.contains(FPlayers.i.get(player))) {
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
        wrappers.get(faction).updatePrefixes();
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

            for (Map.Entry<FScoreboard, Team> entry : teams.entrySet()) {
                FPlayer fplayer = entry.getKey().getFPlayer();
                Team team = entry.getValue();

                String prefix = TL.DEFAULT_PREFIX.toString();
                prefix = prefix.replace("{relationcolor}", faction.getRelationTo(fplayer).getColor().toString());
                prefix = prefix.replace("{faction}", faction.getTag().substring(0, Math.min("{faction}".length() + 16 - prefix.length(), faction.getTag().length())));
                if (team.getPrefix() == null || !team.getPrefix().equals(prefix)) {
                    team.setPrefix(prefix);
                }
            }
        }
    }

    private void addPlayer(OfflinePlayer player) {
        if (members.add(player.getUniqueId())) {
            for (Team team : teams.values()) {
                team.addPlayer(player);
            }
        }
    }

    private void removePlayer(OfflinePlayer player) {
        if (members.remove(player.getUniqueId())) {
            for (Team team : teams.values()) {
                team.removePlayer(player);
            }
        }
    }

    private Set<OfflinePlayer> getPlayers() {
        Set<OfflinePlayer> ret = new HashSet<OfflinePlayer>();
        for (UUID uuid : members) {
            ret.add(Bukkit.getOfflinePlayer(uuid));
        }
        return ret;
    }

    private void unregister() {
        for (Team team : teams.values()) {
            team.unregister();
        }
        teams.clear();
    }
}

