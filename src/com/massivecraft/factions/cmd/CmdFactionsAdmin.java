package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.cmd.arg.ARBoolean;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsAdmin extends FCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsAdmin()
	{
		// Aliases
		this.addAliases("admin");

		// Args
		this.addOptionalArg("on/off", "flip");
		
		// Requirements
		// this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.ADMIN.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
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
