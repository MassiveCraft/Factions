package com.massivecraft.factions.listeners;

import org.bukkit.plugin.Plugin;
import org.bukkit.event.server.ServerListener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import com.massivecraft.factions.Econ;
import com.massivecraft.factions.SpoutFeatures;


public class FactionsServerListener extends ServerListener {
    @Override
    public void onPluginDisable(PluginDisableEvent event) {
		String name = event.getPlugin().getDescription().getName();
        if (Econ.iConomyHooked() && name.equals("iConomy")) {
			Econ.iConomySet(false);
        }
		else if (Econ.essentialsEcoHooked() && name.equals("Essentials")) {
			Econ.essentialsEcoSet(false);
		}
		else if (name.equals("Spout")) {
			SpoutFeatures.setAvailable(false, "");
		}
	}

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
		Plugin plug = event.getPlugin();
		String name = plug.getDescription().getName();
        if (!Econ.iConomyHooked() && name.equals("iConomy") && plug.getClass().getName().equals("com.iConomy.iConomy")) {
			Econ.iConomySet(true);
		}
		else if (!Econ.essentialsEcoHooked() && name.equals("Essentials")) {
			Econ.essentialsEcoSet(true);
        }
		else if (name.equals("Spout")) {
			SpoutFeatures.setAvailable(true, plug.getDescription().getFullName());
		}
    }
}