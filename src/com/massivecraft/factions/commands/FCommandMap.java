package com.massivecraft.factions.commands;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;


public class FCommandMap extends FCommand
{
	public FCommandMap()
	{
		super();
		this.aliases.add("map");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("on/off", "once");
		
		this.permission = Permission.COMMAND_MAP.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		if (this.argIsSet(0))
		{
			if (this.argAsBool(0, ! fme.isMapAutoUpdating()))
			{
				// Turn on

				// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
				if ( ! payForCommand(Conf.econCostMap)) return;

				fme.setMapAutoUpdating(true);
				sendMessageParsed("<i>Map auto update <green>ENABLED.");
				
				// And show the map once
				showMap();
			}
			else
			{
				// Turn off
				fme.setMapAutoUpdating(false);
				sendMessageParsed("<i>Map auto update <red>DISABLED.");
			}
		}
		else
		{
			// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
			if ( ! payForCommand(Conf.econCostMap)) return;

			showMap();
		}
	}
	
	public void showMap()
	{
		sendMessage(Board.getMap(fme.getFaction(), new FLocation(fme), fme.getPlayer().getLocation().getYaw()));
	}
	
}
