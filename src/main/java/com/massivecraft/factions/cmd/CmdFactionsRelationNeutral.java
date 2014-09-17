package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;

public class CmdFactionsRelationNeutral extends CmdFactionsRelationAbstract
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRelationNeutral()
	{
		// Aliases
		this.addAliases("neutral");
		
		// Misc
		this.targetRelation = Rel.NEUTRAL;
	}
	
}
