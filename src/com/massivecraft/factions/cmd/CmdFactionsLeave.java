package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;

public class CmdFactionsLeave extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsLeave()
	{
		// Aliases
		this.addAliases("leave");

		// Requirements
		this.addRequirements(RequirementHasPerm.get(Perm.LEAVE.node));
		this.addRequirements(ReqHasFaction.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		msender.leave();
	}
	
}
