package com.massivecraft.factions.zcore;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.persist.EM;
import com.massivecraft.factions.zcore.persist.Entity;
import com.massivecraft.factions.zcore.persist.EntityCollection;
import com.massivecraft.factions.zcore.persist.PlayerEntityCollection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

    // We're now using FCommandHandler for this to do things properly.
    //@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (p.handleCommand(event.getPlayer(), event.getMessage())) {
            if (p.logPlayerCommands()) {
                Bukkit.getLogger().info("[PLAYER_COMMAND] " + event.getPlayer().getName() + ": " + event.getMessage());
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (p.handleCommand(event.getPlayer(), event.getMessage(), false, true)) {
            if (p.logPlayerCommands()) {
                Bukkit.getLogger().info("[PLAYER_COMMAND] " + event.getPlayer().getName() + ": " + event.getMessage());
            }
            event.setCancelled(true);
        }

        Player speaker = event.getPlayer();
        String format = event.getFormat();
        format = format.replace(Conf.chatTagReplaceString, P.p.getPlayerFactionTag(speaker)).replace("[FACTION_TITLE]", P.p.getPlayerTitle(speaker));
        event.setFormat(format);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreLogin(PlayerLoginEvent event) {
        for (EntityCollection<? extends Entity> ecoll : EM.class2Entities.values()) {
            if (ecoll instanceof PlayerEntityCollection) {
                ecoll.get(event.getPlayer().getName());
            }
        }
    }
}
