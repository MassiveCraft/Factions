package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARUPlayer;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsOfficer extends FCommand
{
	public CmdFactionsOfficer()
	{
		this.addAliases("officer");
		
		this.addRequiredArg("player");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.OFFICER.node));
	}
	
	@Override
	public void perform()
	{
		UPlayer you = this.arg(0, ARUPlayer.getStartAny(sender));
		if (you == null) return;

		boolean permAny = Perm.OFFICER_ANY.has(sender, false);
		Faction targetFaction = you.getFaction();

		if (targetFaction != usenderFaction && !permAny)
		{
			msg("%s<b> is not a member in your faction.", you.describeTo(usender, true));
			return;
		}
		
		if (usender != null && usender.getRole() != Rel.LEADER && !permAny)
		{
			msg("<b>You are not the faction leader.");
			return;
		}

		if (you == usender && !permAny)
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
			msg("<i>You have removed officer status from %s<i>.", you.describeTo(usender, true));
		}
		else
		{
			// Give
			you.setRole(Rel.OFFICER);
			targetFaction.msg("%s<i> was promoted to officer in your faction.", you.describeTo(targetFaction, true));
			msg("<i>You have promoted %s<i> to officer.", you.describeTo(usender, true));
		}
	}
	
}
