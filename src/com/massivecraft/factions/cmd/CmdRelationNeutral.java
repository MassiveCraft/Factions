package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Rel;

public class CmdRelationNeutral extends FRelationCommand
{
	public CmdRelationNeutral()
	{
		aliases.add("neutral");
		targetRelation = Rel.NEUTRAL;
	}
}
