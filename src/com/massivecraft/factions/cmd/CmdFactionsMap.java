package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.massivecore.cmd.arg.ARBoolean;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;
import com.massivecraft.massivecore.ps.PS;

public class CmdFactionsMap extends FCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsMap()
	{
		// Aliases
		this.addAliases("map");

		// Args
		this.addOptionalArg("on/off", "once");

		// Requirements
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.MAP.node));
		this.addRequirements(ReqIsPlayer.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		if (!this.argIsSet(0))
		{
			showMap();
			return;
		}
		
		if (this.arg(0, ARBoolean.get(), !msender.isMapAutoUpdating()))
		{
			// And show the map once
			showMap();
			
			// Turn on
			msender.setMapAutoUpdating(true);
			msg("<i>Map auto update <green>ENABLED.");
		}
		else
		{
			// Turn off
			msender.setMapAutoUpdating(false);
			msg("<i>Map auto update <red>DISABLED.");
		}
	}
	
	public void showMap()
	{
		sendMessage(BoardColls.get().getMap(usenderFaction, PS.valueOf(me), me.getLocation().getYaw()));
	}
	
}
