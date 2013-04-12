package com.massivecraft.factions.cmd;

import com.massivecraft.factions.BoardColl;
import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.ps.PS;


public class CmdFactionsMap extends FCommand
{
	public CmdFactionsMap()
	{
		super();
		this.aliases.add("map");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("on/off", "once");
		
		this.permission = Perm.MAP.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
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
				if ( ! payForCommand(ConfServer.econCostMap, "to show the map", "for showing the map")) return;

				fme.setMapAutoUpdating(true);
				msg("<i>Map auto update <green>ENABLED.");
				
				// And show the map once
				showMap();
			}
			else
			{
				// Turn off
				fme.setMapAutoUpdating(false);
				msg("<i>Map auto update <red>DISABLED.");
			}
		}
		else
		{
			// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
			if ( ! payForCommand(ConfServer.econCostMap, "to show the map", "for showing the map")) return;

			showMap();
		}
	}
	
	public void showMap()
	{
		sendMessage(BoardColl.get().getMap(myFaction, PS.valueOf(me), fme.getPlayer().getLocation().getYaw()));
	}
	
}
