package com.massivecraft.factions.zcore;

import java.util.Map;

import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListener;

public class MPluginSecretServerListener extends ServerListener
{
	private MPlugin p;
	private String refCommand;

	public MPluginSecretServerListener(MPlugin p)
	{
		this.p = p;
		refCommand = "";

		// attempt to get first command defined in plugin.yml as reference command, if any commands are defined in there
		// reference command will be used to prevent "unknown command" console messages
		try
		{
			@SuppressWarnings("unchecked")
			Map<String, Object> refCmd = (Map<String, Object>) p.getDescription().getCommands();
			if (refCmd != null && !refCmd.isEmpty())
				refCommand = (String)(refCmd.keySet().toArray()[0]);
		}
		catch (ClassCastException ex) {}
	}
	
	@Override
	public void onServerCommand(ServerCommandEvent event)
	{
		if (event.getCommand().length() == 0) return;
		
		if (p.handleCommand(event.getSender(), event.getCommand()))
		{
			event.setCommand(refCommand);
		}
	}
	
}
