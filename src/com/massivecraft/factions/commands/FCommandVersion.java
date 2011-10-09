package com.massivecraft.factions.commands;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;


public class FCommandVersion extends FCommand
{
	public FCommandVersion()
	{
		this.aliases.add("version");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.COMMAND_VERSION.node;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		sendMessageParsed("<i>You are running "+P.p.getDescription().getFullName());
	}
}
