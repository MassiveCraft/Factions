package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;

public class CmdOfficer extends FCommand
{
	
	public CmdOfficer()
	{
		super();
		this.aliases.add("officer");
		
		this.requiredArgs.add("player name");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.OFFICER.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = true;
	}
	
	@Override
	public void perform()
	{
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;
		
		if (you.getFaction() != myFaction)
		{
			msg("%s<b> is not a member in your faction.", you.describeTo(fme, true));
			return;
		}
		
		if (you == fme)
		{
			msg("<b>The target player musn't be yourself.");
			return;
		}

		if (you.getRole() == Rel.OFFICER)
		{
			// Revoke
			you.setRole(Rel.MEMBER);
			myFaction.msg("%s<i> is no longer moderator in your faction.", you.describeTo(myFaction, true));
		}
		else
		{
			// Give
			you.setRole(Rel.OFFICER);
			myFaction.msg("%s<i> was promoted to moderator in your faction.", you.describeTo(myFaction, true));
		}
	}
	
}
