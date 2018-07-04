package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.MassiveException;

public class CmdFactionsRelationCheck extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdFactionsRelationCheck()
	{
		// Parameter
		this.addParameter(TypeFaction.get(), "faction");
		this.addParameter(TypeFaction.get(), "faction", "you");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		final Faction faction1 = this.readArg();
		final Faction faction2 = this.readArg(msenderFaction);
		
		Rel relationBetweenFactions = faction1.getRelationTo(faction2);
		String relationText = relationBetweenFactions.getColor().toString() + relationBetweenFactions.getDescFactionMany();
		if (relationBetweenFactions == Rel.MEMBER && msenderFaction != faction1) relationText = "<silver>the same faction";
		
		msg( "%s <i>and %s <i>are %s<i>.", faction1.getName(msenderFaction), faction2.getName(msenderFaction), relationText);
	}
}
