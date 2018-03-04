package com.massivecraft.factions.util;

import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TL;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClipPlaceholderAPIManager extends EZPlaceholderHook {

    public ClipPlaceholderAPIManager() {
        super(P.p, "factionsuuid");
    }

    @Override
    public String onPlaceholderRequest(Player player, String placeholder) {
        if (player == null || placeholder == null) {
            return "";
        }

        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        Faction faction = fPlayer.getFaction();
        switch (placeholder) {
            // First list player stuff
            case "player_name":
                return fPlayer.getName();
            case "player_lastseen":
                String humanized = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - fPlayer.getLastLoginTime(), true, true) + TL.COMMAND_STATUS_AGOSUFFIX;
                return fPlayer.isOnline() ? ChatColor.GREEN + TL.COMMAND_STATUS_ONLINE.toString() : (System.currentTimeMillis() - fPlayer.getLastLoginTime() < 432000000 ? ChatColor.YELLOW + humanized : ChatColor.RED + humanized);
            case "player_group":
                return P.p.getPrimaryGroup(Bukkit.getOfflinePlayer(UUID.fromString(fPlayer.getId())));
            case "player_balance":
                return Econ.isSetup() ? Econ.getFriendlyBalance(fPlayer) : TL.ECON_OFF.format("balance");
            case "player_power":
                return String.valueOf(fPlayer.getPowerRounded());
            case "player_maxpower":
                return String.valueOf(fPlayer.getPowerMaxRounded());
            case "player_kills":
                return String.valueOf(fPlayer.getKills());
            case "player_deaths":
                return String.valueOf(fPlayer.getDeaths());
            case "player_role":
                return fPlayer.hasFaction() ? fPlayer.getRole().getPrefix() : "";
            // Then Faction stuff
            case "faction_name":
                return faction.getTag();
            case "faction_power":
                return String.valueOf(faction.getPowerRounded());
            case "faction_powermax":
                return String.valueOf(faction.getPowerMaxRounded());
            case "faction_description":
                return faction.getDescription();
            case "faction_claims":
                return String.valueOf(faction.getAllClaims().size());
            case "faction_founded":
                return String.valueOf(TL.sdf.format(faction.getFoundedDate()));
            case "faction_joining":
                return (faction.getOpen() ? TL.COMMAND_SHOW_UNINVITED.toString() : TL.COMMAND_SHOW_INVITATION.toString());
            case "faction_peaceful":
                return faction.isPeaceful() ? Conf.colorNeutral + TL.COMMAND_SHOW_PEACEFUL.toString() : "";
            case "faction_powerboost":
                double powerBoost = faction.getPowerBoost();
                return (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? TL.COMMAND_SHOW_BONUS.toString() : TL.COMMAND_SHOW_PENALTY.toString() + powerBoost + ")");
            case "faction_leader":
                FPlayer fAdmin = faction.getFPlayerAdmin();
                return fAdmin == null ? "Server" : fAdmin.getName().substring(0, fAdmin.getName().length() > 14 ? 13 : fAdmin.getName().length());
            case "faction_warps":
                return String.valueOf(faction.getWarps().size());
            case "faction_raidable":
                boolean raid = P.p.getConfig().getBoolean("hcf.raidable", false) && faction.getLandRounded() >= faction.getPowerRounded();
                return raid ? TL.RAIDABLE_TRUE.toString() : TL.RAIDABLE_FALSE.toString();
            case "faction_home_world":
                return faction.hasHome() ? faction.getHome().getWorld().getName() : "";
            case "faction_home_x":
                return faction.hasHome() ? String.valueOf(faction.getHome().getBlockX()) : "";
            case "faction_home_y":
                return faction.hasHome() ? String.valueOf(faction.getHome().getBlockY()) : "";
            case "faction_home_z":
                return faction.hasHome() ? String.valueOf(faction.getHome().getBlockZ()) : "";
            case "facion_land_value":
                return Econ.shouldBeUsed() ? Econ.moneyString(Econ.calculateTotalLandValue(faction.getLandRounded())) : TL.ECON_OFF.format("value");
            case "faction_land_refund":
                return Econ.shouldBeUsed() ? Econ.moneyString(Econ.calculateTotalLandRefund(faction.getLandRounded())) : TL.ECON_OFF.format("refund");
            case "faction_bank_balance":
                return Econ.shouldBeUsed() ? Econ.moneyString(Econ.getBalance(faction.getAccountId())) : TL.ECON_OFF.format("balance");
            case "faction_allies":
                return String.valueOf(faction.getRelationCount(Relation.ALLY));
            case "faction_enemies":
                return String.valueOf(faction.getRelationCount(Relation.ENEMY));
            case "faction_truces":
                return String.valueOf(faction.getRelationCount(Relation.TRUCE));
            case "faction_online":
                return String.valueOf(faction.getOnlinePlayers().size());
            case "faction_offline":
                return String.valueOf(faction.getFPlayers().size() - faction.getOnlinePlayers().size());
            case "faction_size":
                return String.valueOf(faction.getFPlayers().size());
            case "faction_kills":
                return String.valueOf(faction.getKills());
            case "faction_deaths":
                return String.valueOf(faction.getDeaths());
            case "faction_maxvaults":
                return String.valueOf(faction.getMaxVaults());
        }

        return null;
    }
}
