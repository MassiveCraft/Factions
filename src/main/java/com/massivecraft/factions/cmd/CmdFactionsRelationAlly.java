package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;

public class CmdFactionsRelationAlly extends CmdFactionsRelationAbstract
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRelationAlly()
	{
		// Aliases
		this.addAliases("ally");
		
		// Misc
		this.targetRelation = Rel.ALLY;
	}
	
}
