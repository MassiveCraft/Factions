package com.massivecraft.factions.commands;

import com.massivecraft.factions.struct.Permission;

public class FCommandClaim extends FCommand
{
	
	public FCommandClaim()
	{
		super();
		this.aliases.add("claim");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.COMMAND_CLAIM.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		
		aliases.add("claim");
	}
	
	@Override
	public void perform()
	{
		if( isLocked() )
		{
			sendLockMessage();
			return;
		}
		
		fme.attemptClaim(true);
	}
	
}
