package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsOfficer extends FCommand
{
	
	public CmdFactionsOfficer()
	{
		this.addAliases("officer");
		
		this.addRequiredArg("player");
		
		this.addRequirements(ReqHasPerm.get(Perm.OFFICER.node));
	}
	
	@Override
	public void perform()
	{
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;

		boolean permAny = Perm.OFFICER_ANY.has(sender, false);
		Faction targetFaction = you.getFaction();

		if (targetFaction != myFaction && !permAny)
		{
			msg("%s<b> is not a member in your faction.", you.describeTo(fme, true));
			return;
		}
		
		if (fme != null && fme.getRole() != Rel.LEADER && !permAny)
		{
			msg("<b>You are not the faction leader.");
			return;
		}

		if (you == fme && !permAny)
		{
			msg("<b>The target player musn't be yourself.");
			return;
		}

		if (you.getRole() == Rel.LEADER)
		{
			msg("<b>The target player is a faction leader. Demote them first.");
			return;
		}

		if (you.getRole() == Rel.OFFICER)
		{
			// Revoke
			you.setRole(Rel.MEMBER);
			targetFaction.msg("%s<i> is no longer officer in your faction.", you.describeTo(targetFaction, true));
			msg("<i>You have removed officer status from %s<i>.", you.describeTo(fme, true));
		}
		else
		{
			// Give
			you.setRole(Rel.OFFICER);
			targetFaction.msg("%s<i> was promoted to officer in your faction.", you.describeTo(targetFaction, true));
			msg("<i>You have promoted %s<i> to officer.", you.describeTo(fme, true));
		}
	}
	
}
