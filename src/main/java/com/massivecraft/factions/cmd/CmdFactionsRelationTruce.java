package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;

public class CmdFactionsRelationTruce extends CmdFactionsRelationAbstract
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRelationTruce()
	{
		// Aliases
		this.addAliases("truce");
		
		// Misc
		this.targetRelation = Rel.TRUCE;
	}
	
}
