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
		Boolean target = this.arg(0, ARBoolean.get(), !usender.isUsingAdminMode());
		if (target == null) return;
		
		usender.setUsingAdminMode(target);		
		
		if ( usender.isUsingAdminMode())
		{
			usender.msg("<i>You have enabled admin bypass mode.");
			Factions.get().log(usender.getName() + " has ENABLED admin bypass mode.");
		}
		else
		{
			usender.msg("<i>You have disabled admin bypass mode.");
			Factions.get().log(usender.getName() + " DISABLED admin bypass mode.");
		}
	}
}
