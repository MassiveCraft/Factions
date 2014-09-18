package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARMPlayer;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsPromote extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPromote()
	{
		// Aliases
		this.addAliases("promote");

		// Args
		this.addRequiredArg("player");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.PROMOTE.node));
		
		//To promote someone from recruit -> member you must be an officer.
		//To promote someone from member -> officer you must be a leader.
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
		
		if (you.getFaction() != msenderFaction)
		{
			msg("%s<b> is not a member in your faction.", you.describeTo(msender, true));
			return;
		}
		
		if (you == msender)
		{
			msg("<b>The target player mustn't be yourself.");
			return;
		}

		if (you.getRole() == Rel.RECRUIT)
		{
			if (!msender.getRole().isAtLeast(Rel.OFFICER))
			{
				msg("<b>You must be an officer to promote someone to member.");
				return;
			}
			you.setRole(Rel.MEMBER);
			msenderFaction.msg("%s<i> was promoted to being a member of your faction.", you.describeTo(msenderFaction, true));
		}
		else if (you.getRole() == Rel.MEMBER)
		{
			if (!msender.getRole().isAtLeast(Rel.LEADER))
			{
				msg("<b>You must be the leader to promote someone to officer.");
				return;
			}
			// Give
			you.setRole(Rel.OFFICER);
			msenderFaction.msg("%s<i> was promoted to being a officer in your faction.", you.describeTo(msenderFaction, true));
		}
	}
	
}
