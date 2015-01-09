package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

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
		
		// Subcommands
		this.addSubCommand(this.cmdFactionsFlagList);
		this.addSubCommand(this.cmdFactionsFlagShow);
		this.addSubCommand(this.cmdFactionsFlagSet);
		
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.FLAG.node));
	}
	
}
