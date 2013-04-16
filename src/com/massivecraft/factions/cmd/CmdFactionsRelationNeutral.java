package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;

public class CmdFactionsRelationNeutral extends CmdFactionsRelationAbstract
{
	public CmdFactionsRelationNeutral()
	{
		this.addAliases("neutral");
		
		this.targetRelation = Rel.NEUTRAL;
	}
}
