package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;

public class CmdFactionsRelationEnemy extends CmdFactionsRelationAbstract
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRelationEnemy()
	{
		// Aliases
		this.addAliases("enemy");
		
		// Misc
		this.targetRelation = Rel.ENEMY;
	}
	
}
