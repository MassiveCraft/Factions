package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Rel;

public class CmdRelationAlly extends FRelationCommand
{
	public CmdRelationAlly()
	{
		aliases.add("ally");
		targetRelation = Rel.ALLY;
	}
}
