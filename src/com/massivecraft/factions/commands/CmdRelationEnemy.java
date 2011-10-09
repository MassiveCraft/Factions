package com.massivecraft.factions.commands;

import com.massivecraft.factions.struct.Relation;

public class CmdRelationEnemy extends FRelationCommand
{
	public CmdRelationEnemy()
	{
		aliases.add("enemy");
		targetRelation = Relation.ENEMY;
	}
}
