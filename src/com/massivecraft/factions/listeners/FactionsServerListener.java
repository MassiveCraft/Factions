package com.massivecraft.factions.listeners;

import org.bukkit.plugin.Plugin;
import org.bukkit.event.server.ServerListener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Econ;
import com.massivecraft.factions.Factions;

import com.iConomy.*;


public class FactionsServerListener extends ServerListener {
    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        if (Econ.iConomyHooked()) {
            if (event.getPlugin().getDescription().getName().equals("iConomy")) {
                Econ.iConomySet(null);
				Factions.log("Un-hooked from iConomy.");
            }
        }
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        if (!Econ.iConomyHooked()) {
            Plugin iConomy = Factions.instance.getServer().getPluginManager().getPlugin("iConomy");

            if (iConomy != null) {
                if (iConomy.isEnabled() && iConomy.getClass().getName().equals("com.iConomy.iConomy")) {
	                Econ.iConomySet((iConomy)iConomy);
					Factions.log("Hooked into iConomy, "+(Conf.econIConomyEnabled ? "and interface is enabled" : "but interface is currently disabled (\"econIConomyEnabled\": false)")+".");
                }
            }
        }
    }
}