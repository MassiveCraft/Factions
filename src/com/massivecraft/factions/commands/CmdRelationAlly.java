package com.massivecraft.factions.commands;

import com.massivecraft.factions.struct.Relation;

public class CmdRelationAlly extends FRelationCommand
{
	public CmdRelationAlly()
	{
		aliases.add("ally");
		targetRelation = Relation.ALLY;
	}
}
