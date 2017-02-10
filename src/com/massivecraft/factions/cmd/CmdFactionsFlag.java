package com.massivecraft.factions.cmd;

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
		// Children
		this.addChild(this.cmdFactionsFlagList);
		this.addChild(this.cmdFactionsFlagShow);
		this.addChild(this.cmdFactionsFlagSet);
	}
	
}
