package com.massivecraft.factions.cmd;

import java.util.List;

import org.bukkit.Location;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeBoolean;
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

		// Parameters
		this.addParameter(TypeBoolean.getOn(), "on/off", "once");

		// Requirements
		this.addRequirements(RequirementHasPerm.get(Perm.MAP.node));
		this.addRequirements(RequirementIsPlayer.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		if ( ! this.argIsSet())
		{
			showMap(Const.MAP_WIDTH, Const.MAP_HEIGHT_FULL);
			return;
		}
		
		if (this.readArg(!msender.isMapAutoUpdating()))
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
		message(message);
	}
	
}
