package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.req.ReqHasFaction;

import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;

public class CmdFactionsRelation extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	public CmdFactionsRelationSet cmdFactionsRelationSet = new CmdFactionsRelationSet();
	public CmdFactionsRelationList cmdFactionsRelationList = new CmdFactionsRelationList();
	public CmdFactionsRelationWishes cmdFactionsRelationWishes = new CmdFactionsRelationWishes();

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdFactionsRelation()
	{
		// Children
		this.addChild(this.cmdFactionsRelationSet);
		this.addChild(this.cmdFactionsRelationList);
		this.addChild(this.cmdFactionsRelationWishes);

		// Aliases
		this.addAliases("relation");

		// Requirements
		this.addRequirements(RequirementHasPerm.get(Perm.RELATION.node));
		this.addRequirements(ReqHasFaction.get());
	}

}
