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
			msender.msg("<i>你开启了管理员bypass模式.");
			Factions.get().log(msender.getId() + " 开启管理员bypass模式.");
		}
		else
		{
			msender.msg("<i>你禁止了管理员bypass模式.");
			Factions.get().log(msender.getId() + " 禁止管理员bypass模式.");
		}
	}
}
