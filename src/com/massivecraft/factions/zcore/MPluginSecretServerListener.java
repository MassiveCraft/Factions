package com.massivecraft.factions.zcore;

import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public class MPluginSecretServerListener implements Listener
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
			Map<String, Map<String, Object>> refCmd = p.getDescription().getCommands();
			if (refCmd != null && !refCmd.isEmpty())
				refCommand = (String)(refCmd.keySet().toArray()[0]);
		}
		catch (ClassCastException ex) {}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onServerCommand(ServerCommandEvent event)
	{
		if (event.getCommand().length() == 0) return;
		
		if (p.handleCommand(event.getSender(), event.getCommand()))
		{
			event.setCommand(refCommand);
		}
	}
	
}
