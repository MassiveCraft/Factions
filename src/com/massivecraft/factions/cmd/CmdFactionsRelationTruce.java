package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;

public class CmdFactionsRelationTruce extends CmdFactionsRelationAbstract
{
	public CmdFactionsRelationTruce()
	{
		aliases.add("truce");
		targetRelation = Rel.TRUCE;
	}
}
