package com.massivecraft.factions.commands;

import com.massivecraft.factions.struct.Relation;

public class FCommandRelationAlly extends FRelationCommand
{
	public FCommandRelationAlly()
	{
		aliases.add("ally");
		targetRelation = Relation.ALLY;
	}
}
