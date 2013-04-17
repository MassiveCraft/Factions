package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARFPlayer;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsDeinvite extends FCommand
{
	
	public CmdFactionsDeinvite()
	{
		this.addAliases("deinvite", "deinv");
		
		this.addRequiredArg("player");
		
		this.addRequirements(ReqHasPerm.get(Perm.DEINVITE.node));
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
			msg("<i>You might want to: %s", Factions.get().getOuterCmdFactions().cmdFactionsKick.getUseageTemplate(false));
			return;
		}
		
		myFaction.deinvite(you);
		
		you.msg("%s<i> revoked your invitation to <h>%s<i>.", fme.describeTo(you), myFaction.describeTo(you));
		
		myFaction.msg("%s<i> revoked %s's<i> invitation.", fme.describeTo(myFaction), you.describeTo(myFaction));
	}
	
}
