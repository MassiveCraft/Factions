package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsAccessView extends CmdFactionsAccessAbstract
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsAccessView()
	{
		// Aliases
		this.addAliases("v", "view");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.ACCESS_VIEW.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void innerPerform()
	{
		this.sendAccessInfo();
	}
	
}
