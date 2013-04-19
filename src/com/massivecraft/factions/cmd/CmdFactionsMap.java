package com.massivecraft.factions.cmd;

import com.massivecraft.factions.BoardColl;
import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.cmd.arg.ARBoolean;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;
import com.massivecraft.mcore.ps.PS;


public class CmdFactionsMap extends FCommand
{
	public CmdFactionsMap()
	{
		this.addAliases("map");
		
		this.addOptionalArg("on/off", "once");
		
		this.addRequirements(ReqHasPerm.get(Perm.MAP.node));
		this.addRequirements(ReqIsPlayer.get());
	}
	
	@Override
	public void perform()
	{
		if (!this.argIsSet(0))
		{
			// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
			if (!payForCommand(ConfServer.econCostMap)) return;

			showMap();
			return;
		}
		
		if (this.arg(0, ARBoolean.get(), !fme.isMapAutoUpdating()))
		{
			// Turn on

			// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
			if (!payForCommand(ConfServer.econCostMap)) return;

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
	
	public void showMap()
	{
		sendMessage(BoardColl.get().getMap(myFaction, PS.valueOf(me), fme.getPlayer().getLocation().getYaw()));
	}
	
}
