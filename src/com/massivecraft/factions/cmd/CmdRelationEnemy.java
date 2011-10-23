package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Rel;

public class CmdRelationEnemy extends FRelationCommand
{
	public CmdRelationEnemy()
	{
		aliases.add("enemy");
		targetRelation = Rel.ENEMY;
	}
}
