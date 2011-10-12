package com.massivecraft.factions.listeners;

import org.bukkit.plugin.Plugin;
import org.bukkit.event.server.ServerListener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.SpoutFeatures;


public class FactionsServerListener extends ServerListener
{
	public P p;
	public FactionsServerListener(P p)
	{
		this.p = p;
	}
	
	@Override
	public void onPluginDisable(PluginDisableEvent event)
	{
		String name = event.getPlugin().getDescription().getName();
		if (name.equals("Spout"))
		{
			SpoutFeatures.setAvailable(false, "");
		}
	}

	@Override
	public void onPluginEnable(PluginEnableEvent event)
	{
		Plugin plug = event.getPlugin();
		String name = plug.getDescription().getName();
		if (name.equals("Spout"))
		{
			SpoutFeatures.setAvailable(true, plug.getDescription().getFullName());
		}
	}
}