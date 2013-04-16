package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;


public class CmdFactionsVersion extends FCommand
{
	public CmdFactionsVersion()
	{
		this.addAliases("version");
		this.addRequirements(ReqHasPerm.get(Perm.VERSION.node));
	}

	@Override
	public void perform()
	{
		msg("<i>You are running "+Factions.get().getDescription().getFullName());
	}
}
