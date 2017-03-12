package com.massivecraft.factions.cmd;

import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;

public class CmdFactionsAccess extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public CmdFactionsAccessView cmdFactionsAccessView = new CmdFactionsAccessView();
	public CmdFactionsAccessPlayer cmdFactionsAccessPlayer = new CmdFactionsAccessPlayer();
	public CmdFactionsAccessFaction cmdFactionsAccessFaction = new CmdFactionsAccessFaction();
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsAccess()
	{
		// Requirements
		this.addRequirements(RequirementIsPlayer.get());
	}
	
}
