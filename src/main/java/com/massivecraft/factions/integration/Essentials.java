package com.massivecraft.factions.integration;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Teleport;
import com.earth2me.essentials.Trade;
import com.massivecraft.factions.Conf;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Essentials {

    private static IEssentials essentials;

    public static void setup() {
        Plugin ess = Bukkit.getPluginManager().getPlugin("Essentials");
        if (ess != null) {
            essentials = (IEssentials) ess;
        }
    }

    // return false if feature is disabled or Essentials isn't available
    public static boolean handleTeleport(Player player, Location loc) {
        if (!Conf.homesTeleportCommandEssentialsIntegration || essentials == null) {
            return false;
        }

        Teleport teleport = essentials.getUser(player).getTeleport();
        Trade trade = new Trade(Conf.econCostHome, essentials);
        try {
            teleport.teleport(loc, trade);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED.toString() + e.getMessage());
        }
        return true;
    }

    public static boolean isVanished(Player player) {
        if (essentials == null) {
            return false;
        }
        return essentials.getUser(player).isVanished();
    }
}
