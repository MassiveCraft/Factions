package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARUPlayer;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsPromote extends FCommand
{
	public CmdFactionsPromote()
	{
		this.addAliases("promote");
		
		this.addRequiredArg("player");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.PROMOTE.node));
		
		//To promote someone from recruit -> member you must be an officer.
		//To promote someone from member -> officer you must be a leader.
		//We'll handle this internally
	}
	
	@Override
	public void perform()
	{
		UPlayer you = this.arg(0, ARUPlayer.getStartAny(sender));
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

		if (you.getRole() == Rel.RECRUIT)
		{
			if (!usender.getRole().isAtLeast(Rel.OFFICER))
			{
				msg("<b>You must be an officer to promote someone to member.");
				return;
			}
			you.setRole(Rel.MEMBER);
			usenderFaction.msg("%s<i> was promoted to being a member of your faction.", you.describeTo(usenderFaction, true));
		}
		else if (you.getRole() == Rel.MEMBER)
		{
			if (!usender.getRole().isAtLeast(Rel.LEADER))
			{
				msg("<b>You must be the leader to promote someone to officer.");
				return;
			}
			// Give
			you.setRole(Rel.OFFICER);
			usenderFaction.msg("%s<i> was promoted to being a officer in your faction.", you.describeTo(usenderFaction, true));
		}
	}
	
}
