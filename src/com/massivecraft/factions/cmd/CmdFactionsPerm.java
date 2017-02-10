package com.massivecraft.factions.cmd;

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
		// Children
		this.addChild(this.cmdFactionsPermList);
		this.addChild(this.cmdFactionsPermShow);
		this.addChild(this.cmdFactionsPermSet);
	}
	
}
