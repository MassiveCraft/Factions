package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Visibility;
import com.massivecraft.massivecore.command.type.TypeNullable;

public class CmdFactionsUsed extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsUsed()
	{
		// Parameters
		this.addParameter(TypeNullable.get(TypeFaction.get()), "faction", "show");
		
		// Visibility
		this.setVisibility(Visibility.SECRET);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Is show?
		if (!this.argIsSet(0))
		{
			Faction faction = msender.getUsedFaction();
			msg("<i>Your used faction is %s.", faction.describeTo(msender));
		}
		
		// Parameter
		Faction faction = this.readArg();
		
		// Apply
		msender.setUsedFaction(faction);
		
		// Inform
		String message = "<g>Your used faction was <h>%s<g>.";
		String result = faction == null ? "unset." : "set to " + faction.describeTo(msender, true);
		msg(message, result);
	}
	
}
