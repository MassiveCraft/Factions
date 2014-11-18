package com.massivecraft.factions.cmd;

import java.util.List;

import org.bukkit.Location;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.massivecore.cmd.arg.ARBoolean;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;
import com.massivecraft.massivecore.ps.PS;

public class CmdFactionsMap extends FactionsCommand
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
		this.addRequirements(ReqHasPerm.get(Perm.MAP.node));
		this.addRequirements(ReqIsPlayer.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		if ( ! this.argIsSet(0))
		{
			showMap(Const.MAP_WIDTH, Const.MAP_HEIGHT_FULL);
			return;
		}
		
		if (this.arg(0, ARBoolean.get(), !msender.isMapAutoUpdating()))
		{
			// And show the map once
			showMap(Const.MAP_WIDTH, Const.MAP_HEIGHT);
			
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
	
	public void showMap(int width, int height)
	{
		Location location = me.getLocation();
		List<String> message = BoardColl.get().getMap(msenderFaction, PS.valueOf(location), location.getYaw(), width, height);
		sendMessage(message);
	}
	
}
