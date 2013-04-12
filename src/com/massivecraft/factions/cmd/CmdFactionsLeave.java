package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;

public class CmdFactionsLeave extends FCommand {
	
	public CmdFactionsLeave()
	{
		super();
		this.aliases.add("leave");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Perm.LEAVE.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		fme.leave(true);
	}
	
}
