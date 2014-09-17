package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARMPlayer;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsDemote extends FCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsDemote()
	{
		// Aliases
		this.addAliases("demote");

		// Args
		this.addRequiredArg("player");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.DEMOTE.node));
		
		//To demote someone from member -> recruit you must be an officer.
		//To demote someone from officer -> member you must be a leader.
		//We'll handle this internally
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{	
		MPlayer you = this.arg(0, ARMPlayer.getAny());
		if (you == null) return;
		
		if (you.getFaction() != usenderFaction)
		{
			msg("%s<b> is not a member in your faction.", you.describeTo(usender, true));
			return;
		}
		
		if (you == usender)
		{
			msg("<b>The target player mustn't be yourself.");
			return;
		}

		if (you.getRole() == Rel.MEMBER)
		{
			if (!usender.getRole().isAtLeast(Rel.OFFICER))
			{
				msg("<b>You must be an officer to demote a member to recruit.");
				return;
			}
			you.setRole(Rel.RECRUIT);
			usenderFaction.msg("%s<i> was demoted to being a recruit in your faction.", you.describeTo(usenderFaction, true));
		}
		else if (you.getRole() == Rel.OFFICER)
		{
			if (!usender.getRole().isAtLeast(Rel.LEADER))
			{
				msg("<b>You must be the leader to demote an officer to member.");
				return;
			}
			you.setRole(Rel.MEMBER);
			usenderFaction.msg("%s<i> was demoted to being a member in your faction.", you.describeTo(usenderFaction, true));
		}
	}
	
}
