package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsDeinvite extends FCommand
{
	
	public CmdFactionsDeinvite()
	{
		super();
		
		this.addAliases("deinvite", "deinv");
		
		this.requiredArgs.add("player");
		//this.optionalArgs.put("", "");
		
		this.addRequirements(ReqHasPerm.get(Perm.DEINVITE.node));
		
		senderMustBeMember = false;
		senderMustBeOfficer = true;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;
		
		if (you.getFaction() == myFaction)
		{
			msg("%s<i> is already a member of %s", you.getName(), myFaction.getTag());
			msg("<i>You might want to: %s", p.cmdBase.cmdFactionsKick.getUseageTemplate(false));
			return;
		}
		
		myFaction.deinvite(you);
		
		you.msg("%s<i> revoked your invitation to <h>%s<i>.", fme.describeTo(you), myFaction.describeTo(you));
		
		myFaction.msg("%s<i> revoked %s's<i> invitation.", fme.describeTo(myFaction), you.describeTo(myFaction));
	}
	
}
