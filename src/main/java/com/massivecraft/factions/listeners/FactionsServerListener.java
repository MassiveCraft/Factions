package com.massivecraft.factions.listeners;

import com.massivecraft.factions.P;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;


public class FactionsServerListener implements Listener {
    public P p;

    public FactionsServerListener(P p) {
        this.p = p;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
        String name = event.getPlugin().getDescription().getName();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
        Plugin plug = event.getPlugin();
        String name = plug.getDescription().getName();
    }
}