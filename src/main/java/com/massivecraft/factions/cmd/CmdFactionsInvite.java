package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARMPlayer;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsInvitedChange;
import com.massivecraft.massivecore.cmd.arg.ARBoolean;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;

public class CmdFactionsInvite extends FCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsInvite()
	{
		// Aliases
		this.addAliases("inv", "invite");

		// Args
		this.addRequiredArg("player");
		this.addOptionalArg("yes/no", "toggle");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.INVITE.node));
		this.addRequirements(ReqHasFaction.get());
		this.addRequirements(ReqIsPlayer.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		MPlayer mplayer = this.arg(0, ARMPlayer.getAny());
		if (mplayer == null) return;
		
		Boolean newInvited = this.arg(1, ARBoolean.get(), !usenderFaction.isInvited(mplayer));
		if (newInvited == null) return;
		
		// Allready member?
		if (mplayer.getFaction() == usenderFaction)
		{
			msg("%s<i> is already a member of %s", mplayer.getName(), usenderFaction.getName());
			msg("<i>You might want to: " + Factions.get().getOuterCmdFactions().cmdFactionsKick.getUseageTemplate(false));
			return;
		}
		
		// FPerm
		if ( ! FPerm.INVITE.has(usender, usenderFaction, true)) return;
		
		// Event
		EventFactionsInvitedChange event = new EventFactionsInvitedChange(sender, mplayer, usenderFaction, newInvited);
		event.run();
		if (event.isCancelled()) return;
		newInvited = event.isNewInvited();

		// Apply
		usenderFaction.setInvited(mplayer, newInvited);
		
		// Inform
		if (newInvited)
		{
			mplayer.msg("%s<i> invited you to %s", usender.describeTo(mplayer, true), usenderFaction.describeTo(mplayer));
			usenderFaction.msg("%s<i> invited %s<i> to your faction.", usender.describeTo(usenderFaction, true), mplayer.describeTo(usenderFaction));
		}
		else
		{
			mplayer.msg("%s<i> revoked your invitation to <h>%s<i>.", usender.describeTo(mplayer), usenderFaction.describeTo(mplayer));
			usenderFaction.msg("%s<i> revoked %s's<i> invitation.", usender.describeTo(usenderFaction), mplayer.describeTo(usenderFaction));
		}
	}
	
}
