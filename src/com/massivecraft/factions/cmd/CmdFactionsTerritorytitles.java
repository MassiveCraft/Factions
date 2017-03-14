package com.massivecraft.factions.cmd;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Visibility;
import com.massivecraft.massivecore.command.requirement.RequirementTitlesAvailable;
import com.massivecraft.massivecore.command.type.primitive.TypeBooleanOn;
import com.massivecraft.massivecore.mixin.MixinTitle;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsTerritorytitles extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsTerritorytitles()
	{
		// Aliases
		this.addAliases("tt");

		// Parameters
		this.addParameter(TypeBooleanOn.get(), "on|off", "toggle");

		// Requirements
		this.addRequirements(RequirementTitlesAvailable.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Visibility getVisibility()
	{
		// We hide the command if titles aren't available.
		if ( ! MixinTitle.get().isAvailable()) return Visibility.INVISIBLE;
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
