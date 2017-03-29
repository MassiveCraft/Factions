package com.massivecraft.factions.cmd;

import com.massivecraft.factions.util.AsciiMap;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeBooleanYes;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsMap extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsMap()
	{
		// Parameters
		this.addParameter(TypeBooleanYes.get(), "on/off", "once");

		// Requirements
		this.addRequirements(RequirementIsPlayer.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// NOTE: Map show is performed when auto == true || once
		boolean argSet = this.argIsSet();
		boolean showMap = true;
		
		// Auto update
		if (argSet) showMap = this.adjustAutoUpdating();
		if (!showMap) return;
		
		// Show Map
		AsciiMap map = new AsciiMap(msender, me, !argSet);
		message(map.render());
	}
	
	private boolean adjustAutoUpdating() throws MassiveException
	{
		// Get
		boolean autoUpdating = this.readArg(!msender.isMapAutoUpdating());
		
		// Set
		msender.setMapAutoUpdating(autoUpdating);
		
		// Inform
		msg("<i>Map auto update %s<i>.", Txt.parse(autoUpdating ? "<green>ENABLED" : "<red>DISABLED"));
		return autoUpdating;
	}
	
}
