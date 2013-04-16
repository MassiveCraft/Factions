package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;

public class CmdFactionsInvite extends FCommand
{
	public CmdFactionsInvite()
	{
		super();
		this.aliases.add("invite");
		this.aliases.add("inv");
		
		this.requiredArgs.add("player");
		//this.optionalArgs.put("", "");
		
		this.permission = Perm.INVITE.node;
		
		
		this.addRequirements(ReqIsPlayer.get());
		
		senderMustBeOfficer = true;
	}
	
	@Override
	public void perform()
	{
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;
		
		if (you.getFaction() == myFaction)
		{
			msg("%s<i> is already a member of %s", you.getName(), myFaction.getTag());
			msg("<i>You might want to: " +  p.cmdBase.cmdFactionsKick.getUseageTemplate(false));
			return;
		}

		if (fme != null && ! FPerm.INVITE.has(fme, myFaction)) return;
		
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(ConfServer.econCostInvite, "to invite someone", "for inviting someone")) return;

		myFaction.invite(you);
		
		you.msg("%s<i> invited you to %s", fme.describeTo(you, true), myFaction.describeTo(you));
		myFaction.msg("%s<i> invited %s<i> to your faction.", fme.describeTo(myFaction, true), you.describeTo(myFaction));
	}
	
}
