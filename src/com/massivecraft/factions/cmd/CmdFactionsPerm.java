package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

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
		
		// Subcommands
		this.addSubCommand(this.cmdFactionsPermList);
		this.addSubCommand(this.cmdFactionsPermShow);
		this.addSubCommand(this.cmdFactionsPermSet);
		
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.PERM.node));
	}
	
}
