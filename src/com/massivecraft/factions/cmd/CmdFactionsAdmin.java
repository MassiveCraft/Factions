package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.cmd.arg.ARBoolean;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsAdmin extends FCommand
{
	public CmdFactionsAdmin()
	{
		this.addAliases("admin");

		this.addOptionalArg("on/off", "flip");

		this.addRequirements(ReqHasPerm.get(Perm.ADMIN.node));
	}

	@Override
	public void perform()
	{
		Boolean target = this.arg(0, ARBoolean.get(), !fme.isUsingAdminMode());
		if (target == null) return;

		fme.setUsingAdminMode(target);

		if ( fme.isUsingAdminMode())
		{
			fme.msg("<i>You have enabled admin bypass mode.");
			Factions.get().log(fme.getName() + " has ENABLED admin bypass mode.");
		}
		else
		{
			fme.msg("<i>You have disabled admin bypass mode.");
			Factions.get().log(fme.getName() + " DISABLED admin bypass mode.");
		}
	}
}
