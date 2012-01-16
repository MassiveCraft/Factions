package com.massivecraft.factions.integration.capi;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.P;

public class CapiFeatures
{
	public static void setup()
	{
		Plugin plug = Bukkit.getServer().getPluginManager().getPlugin("capi");
		if (plug != null && plug.getClass().getName().equals("com.massivecraft.capi.P"))
		{
			P.p.log("Integration with the CAPI plugin was successful");
			P.p.registerEvent(Event.Type.CUSTOM_EVENT, new PluginCapiListener(P.p), Priority.Normal);
		}
	}
}
