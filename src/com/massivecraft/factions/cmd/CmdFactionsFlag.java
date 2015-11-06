package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;

public class CmdFactionsFlag extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	CmdFactionsFlagList cmdFactionsFlagList = new CmdFactionsFlagList();
	CmdFactionsFlagShow cmdFactionsFlagShow = new CmdFactionsFlagShow();
	CmdFactionsFlagSet cmdFactionsFlagSet = new CmdFactionsFlagSet();
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsFlag()
	{
		// Aliases
		this.addAliases("flag");
		
		// Children
		this.addChild(this.cmdFactionsFlagList);
		this.addChild(this.cmdFactionsFlagShow);
		this.addChild(this.cmdFactionsFlagSet);
		
		// Requirements
		this.addRequirements(RequirementHasPerm.get(Perm.FLAG.node));
	}
	
}
