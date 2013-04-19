package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFPlayer;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;

public class CmdFactionsInvite extends FCommand
{
	public CmdFactionsInvite()
	{
		this.addAliases("inv", "invite");
		
		this.addRequiredArg("player");
		
		this.addRequirements(ReqHasPerm.get(Perm.INVITE.node));
		this.addRequirements(ReqIsPlayer.get());
	}
	
	@Override
	public void perform()
	{
		if ( ! FPerm.INVITE.has(sender, myFaction, true)) return;
		
		FPlayer you = this.arg(0, ARFPlayer.getStartAny());
		if (you == null) return;
		
		if (you.getFaction() == myFaction)
		{
			msg("%s<i> is already a member of %s", you.getName(), myFaction.getTag());
			msg("<i>You might want to: " + Factions.get().getOuterCmdFactions().cmdFactionsKick.getUseageTemplate(false));
			return;
		}

		if (fme != null && ! FPerm.INVITE.has(fme, myFaction)) return;
		
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(ConfServer.econCostInvite)) return;

		myFaction.invite(you);
		
		you.msg("%s<i> invited you to %s", fme.describeTo(you, true), myFaction.describeTo(you));
		myFaction.msg("%s<i> invited %s<i> to your faction.", fme.describeTo(myFaction, true), you.describeTo(myFaction));
	}
	
}
