package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;

public class CmdRelationAlly extends FRelationCommand
{
	public CmdRelationAlly()
	{
		aliases.add("ally");
		targetRelation = Rel.ALLY;
	}
}
