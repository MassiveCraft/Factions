package com.massivecraft.factions.zcore;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.listeners.FactionsPlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class MPluginSecretPlayerListener implements Listener {

    private MPlugin p;

    public MPluginSecretPlayerListener(MPlugin p) {
        this.p = p;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (FactionsPlayerListener.preventCommand(event.getMessage(), event.getPlayer())) {
            if (p.logPlayerCommands()) {
                Bukkit.getLogger().info("[PLAYER_COMMAND] " + event.getPlayer().getName() + ": " + event.getMessage());
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreLogin(PlayerLoginEvent event) {
        FPlayers.getInstance().getByPlayer(event.getPlayer());
    }
}
