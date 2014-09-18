package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARMPlayer;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsOfficer extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsOfficer()
	{
		// Aliases
		this.addAliases("officer");

		// Args
		this.addRequiredArg("player");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.OFFICER.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		MPlayer you = this.arg(0, ARMPlayer.getAny());
		if (you == null) return;

		boolean permAny = Perm.OFFICER_ANY.has(sender, false);
		Faction targetFaction = you.getFaction();

		if (targetFaction != msenderFaction && !permAny)
		{
			msg("%s<b> is not a member in your faction.", you.describeTo(msender, true));
			return;
		}
		
		if (msender != null && msender.getRole() != Rel.LEADER && !permAny)
		{
			msg("<b>You are not the faction leader.");
			return;
		}

		if (you == msender && !permAny)
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
			msg("<i>You have removed officer status from %s<i>.", you.describeTo(msender, true));
		}
		else
		{
			// Give
			you.setRole(Rel.OFFICER);
			targetFaction.msg("%s<i> was promoted to officer in your faction.", you.describeTo(targetFaction, true));
			msg("<i>You have promoted %s<i> to officer.", you.describeTo(msender, true));
		}
	}
	
}
