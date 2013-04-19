package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFPlayer;
import com.massivecraft.factions.event.FactionsEventInvitedChange;
import com.massivecraft.mcore.cmd.arg.ARBoolean;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;

public class CmdFactionsInvite extends FCommand
{
	public CmdFactionsInvite()
	{
		this.addAliases("inv", "invite");
		
		this.addRequiredArg("player");
		this.addOptionalArg("yes/no", "toggle");
		
		this.addRequirements(ReqHasPerm.get(Perm.INVITE.node));
		this.addRequirements(ReqIsPlayer.get());
	}
	
	@Override
	public void perform()
	{
		// Args
		FPlayer fplayer = this.arg(0, ARFPlayer.getStartAny());
		if (fplayer == null) return;
		
		Boolean newInvited = this.arg(1, ARBoolean.get(), !myFaction.isInvited(fplayer));
		if (newInvited == null) return;
		
		// Allready member?
		if (fplayer.getFaction() == myFaction)
		{
			msg("%s<i> is already a member of %s", fplayer.getName(), myFaction.getTag());
			msg("<i>You might want to: " + Factions.get().getOuterCmdFactions().cmdFactionsKick.getUseageTemplate(false));
			return;
		}
		
		// FPerm
		if ( ! FPerm.INVITE.has(sender, myFaction, true)) return;
		
		// Event
		FactionsEventInvitedChange event = new FactionsEventInvitedChange(sender, fplayer, myFaction, newInvited);
		event.run();
		if (event.isCancelled()) return;
		newInvited = event.isNewInvited();

		// Apply
		myFaction.setInvited(fplayer, newInvited);
		
		// Inform
		if (newInvited)
		{
			fplayer.msg("%s<i> invited you to %s", fme.describeTo(fplayer, true), myFaction.describeTo(fplayer));
			myFaction.msg("%s<i> invited %s<i> to your faction.", fme.describeTo(myFaction, true), fplayer.describeTo(myFaction));
		}
		else
		{
			fplayer.msg("%s<i> revoked your invitation to <h>%s<i>.", fme.describeTo(fplayer), myFaction.describeTo(fplayer));
			myFaction.msg("%s<i> revoked %s's<i> invitation.", fme.describeTo(myFaction), fplayer.describeTo(myFaction));
		}
	}
	
}
