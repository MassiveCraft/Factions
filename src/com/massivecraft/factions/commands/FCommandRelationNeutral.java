package com.massivecraft.factions.commands;

import com.massivecraft.factions.struct.Relation;

public class FCommandRelationNeutral extends FRelationCommand
{
	public FCommandRelationNeutral()
	{
		aliases.add("neutral");
		targetRelation = Relation.NEUTRAL;
	}
}
