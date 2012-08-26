package com.massivecraft.factions.integration.herochat;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.P;

public class HerochatFeatures implements Listener
{
	public static void setup()
	{
		Plugin plug = Bukkit.getServer().getPluginManager().getPlugin("Herochat");
		if (plug == null) return;
		if (!plug.getClass().getName().equals("com.dthielke.herochat.Herochat")) return;
		Bukkit.getPluginManager().registerEvents(new HerochatListener(P.p), P.p);
		P.p.log("Integration with Herochat successful");
	}
}
