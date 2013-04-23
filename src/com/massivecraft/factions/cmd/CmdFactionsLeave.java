package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsLeave extends FCommand {

	public CmdFactionsLeave()
	{
		this.addAliases("leave");

		this.addRequirements(ReqHasPerm.get(Perm.LEAVE.node));
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.RECRUIT));
	}

	@Override
	public void perform()
	{
		fme.leave(true);
	}

}
