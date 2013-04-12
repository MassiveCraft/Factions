package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;


public class CmdFactionsVersion extends FCommand
{
	public CmdFactionsVersion()
	{
		this.aliases.add("version");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Perm.VERSION.node;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}

	@Override
	public void perform()
	{
		msg("<i>You are running "+Factions.get().getDescription().getFullName());
	}
}
