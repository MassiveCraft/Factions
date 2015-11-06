package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Visibility;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.requirement.RequirementTitlesAvailable;
import com.massivecraft.massivecore.command.type.primitive.TypeBoolean;
import com.massivecraft.massivecore.mixin.Mixin;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsTerritorytitles extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsTerritorytitles()
	{
		// Aliases
		this.addAliases("tt", "territorytitles");

		// Parameters
		this.addParameter(TypeBoolean.getOn(), "on|off", "toggle");

		// Requirements
		this.addRequirements(RequirementHasPerm.get(Perm.TERRITORYTITLES.node));
		this.addRequirements(RequirementTitlesAvailable.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Visibility getVisibility()
	{
		// We hide the command if titles aren't available.
		if ( ! Mixin.isTitlesAvailable()) return Visibility.INVISIBLE;
		return super.getVisibility();
	}
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		boolean before = msender.isTerritoryInfoTitles();
		boolean after = this.readArg(!before);
		String desc = Txt.parse(after ? "<g>ON" : "<b>OFF");
		
		// NoChange
		if (after == before)
		{
			msg("<i>Territory titles is already %s<i>.", desc);
			return;
		}
		
		// Apply
		msender.setTerritoryInfoTitles(after);
		
		// Inform
		msg("<i>Territory titles is now %s<i>.", desc);
	}
	
}
