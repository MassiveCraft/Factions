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
		
		//this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.ADMIN.node));
	}
	
	@Override
	public void perform()
	{			
		Boolean target = this.arg(0, ARBoolean.get(), !msender.isUsingAdminMode());
		if (target == null) return;
		
		msender.setUsingAdminMode(target);		
		
		if (msender.isUsingAdminMode())
		{
			msender.msg("<i>You have enabled admin bypass mode.");
			Factions.get().log(msender.getId() + " has ENABLED admin bypass mode.");
		}
		else
		{
			msender.msg("<i>You have disabled admin bypass mode.");
			Factions.get().log(msender.getId() + " DISABLED admin bypass mode.");
		}
	}
}
