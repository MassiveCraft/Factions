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
		
		senderMustBeMember = true;
	}
	
	@Override
	public void perform()
	{
		fme.leave(true);
	}
	
}
