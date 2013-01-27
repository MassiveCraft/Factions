package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;

public class CmdPromote extends FCommand
{
	
	public CmdPromote()
	{
		super();
		this.aliases.add("promote");
		
		this.requiredArgs.add("player name");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.PROMOTE.node;
		this.disableOnLock = true;
		
		//To promote someone from recruit -> member you must be an officer.
		//To promote someone from member -> officer you must be a leader.
		//We'll handle this internally
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
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
			msg("<b>The target player mustn't be yourself.");
			return;
		}

		if (you.getRole() == Rel.RECRUIT)
		{
			if (!fme.getRole().isAtLeast(Rel.OFFICER)) {
				msg("<b>You must be an officer to promote someone to member.");
				return;
			}
			you.setRole(Rel.MEMBER);
			myFaction.msg("%s<i> was promoted to being a member of your faction.", you.describeTo(myFaction, true));
		}
		else if (you.getRole() == Rel.MEMBER)
		{
			if (!fme.getRole().isAtLeast(Rel.LEADER)) {
				msg("<b>You must be the leader to promote someone to officer.");
				return;
			}
			// Give
			you.setRole(Rel.OFFICER);
			myFaction.msg("%s<i> was promoted to being a officer in your faction.", you.describeTo(myFaction, true));
		}
	}
	
}
