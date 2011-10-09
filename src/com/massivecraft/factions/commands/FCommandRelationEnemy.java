package com.massivecraft.factions.commands;

import com.massivecraft.factions.struct.Relation;

public class FCommandRelationEnemy extends FRelationCommand
{
	public FCommandRelationEnemy()
	{
		aliases.add("enemy");
		targetRelation = Relation.ENEMY;
	}
}
