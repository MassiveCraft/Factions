package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;

public class CmdFactionsPerm extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	CmdFactionsPermList cmdFactionsPermList = new CmdFactionsPermList();
	CmdFactionsPermShow cmdFactionsPermShow = new CmdFactionsPermShow();
	CmdFactionsPermSet cmdFactionsPermSet = new CmdFactionsPermSet();
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPerm()
	{
		// Aliases
		this.addAliases("perm");
		
		// Children
		this.addChild(this.cmdFactionsPermList);
		this.addChild(this.cmdFactionsPermShow);
		this.addChild(this.cmdFactionsPermSet);
		
		// Requirements
		this.addRequirements(RequirementHasPerm.get(Perm.PERM.node));
	}
	
}
