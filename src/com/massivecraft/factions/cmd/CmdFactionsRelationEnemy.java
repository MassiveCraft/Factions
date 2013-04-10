package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;

public class CmdFactionsRelationEnemy extends CmdFactionsRelationAbstract
{
	public CmdFactionsRelationEnemy()
	{
		aliases.add("enemy");
		targetRelation = Rel.ENEMY;
	}
}
