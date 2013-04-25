package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsLeave extends FCommand {
	
	public CmdFactionsLeave()
	{
		this.addAliases("leave");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.LEAVE.node));
		this.addRequirements(ReqHasFaction.get());
	}
	
	@Override
	public void perform()
	{
		usender.leave();
	}
	
}
