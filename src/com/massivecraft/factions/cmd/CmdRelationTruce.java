package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;

public class CmdRelationTruce extends FRelationCommand
{
	public CmdRelationTruce()
	{
		aliases.add("truce");
		targetRelation = Rel.TRUCE;
	}
}
