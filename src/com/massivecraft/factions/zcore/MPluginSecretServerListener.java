package com.massivecraft.factions.zcore;

import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListener;

public class MPluginSecretServerListener extends ServerListener
{
	private MPlugin p;
	public MPluginSecretServerListener(MPlugin p)
	{
		this.p = p;
	}
	
	// This method is not perfect. It says unknown console command.
	@Override
	public void onServerCommand(ServerCommandEvent event)
	{
		if (event.getCommand().length() == 0) return;
		
		if (p.handleCommand(event.getSender(), event.getCommand()))
		{
			event.setCommand("");
		}
	}
	
}
