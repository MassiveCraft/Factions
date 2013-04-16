package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsAdmin extends FCommand
{
	public CmdFactionsAdmin()
	{
		super();
		
		this.addAliases("admin");
		
		this.optionalArgs.put("on/off", "flip");
		
		this.addRequirements(ReqHasPerm.get(Perm.ADMIN.node));
	}
	
	@Override
	public void perform()
	{
		fme.setHasAdminMode(this.argAsBool(0, ! fme.hasAdminMode()));
		
		if ( fme.hasAdminMode())
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
