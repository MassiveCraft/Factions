package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARUPlayer;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsDemote extends FCommand
{

	public CmdFactionsDemote()
	{
		this.addAliases("demote");

		this.addRequiredArg("player");

		this.addRequirements(ReqHasPerm.get(Perm.DEMOTE.node));

		//To demote someone from member -> recruit you must be an officer.
		//To demote someone from officer -> member you must be a leader.
		//We'll handle this internally
	}

	@Override
	public void perform()
	{
		UPlayer you = this.arg(0, ARUPlayer.getStartAny(fme));
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

		if (you.getRole() == Rel.MEMBER)
		{
			if (!fme.getRole().isAtLeast(Rel.OFFICER))
			{
				msg("<b>You must be an officer to demote a member to recruit.");
				return;
			}
			you.setRole(Rel.RECRUIT);
			myFaction.msg("%s<i> was demoted to being a recruit in your faction.", you.describeTo(myFaction, true));
		}
		else if (you.getRole() == Rel.OFFICER)
		{
			if (!fme.getRole().isAtLeast(Rel.LEADER))
			{
				msg("<b>You must be the leader to demote an officer to member.");
				return;
			}
			you.setRole(Rel.MEMBER);
			myFaction.msg("%s<i> was demoted to being a member in your faction.", you.describeTo(myFaction, true));
		}
	}

}
