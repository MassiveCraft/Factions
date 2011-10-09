package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;

public class CmdClaim extends FCommand
{
	
	public CmdClaim()
	{
		super();
		this.aliases.add("claim");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.CLAIM.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		
		aliases.add("claim");
	}
	
	@Override
	public void perform()
	{
		fme.attemptClaim(true);
	}
	
}
