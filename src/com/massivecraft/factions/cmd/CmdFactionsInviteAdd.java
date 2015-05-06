package com.massivecraft.factions.cmd;

import java.util.Collection;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARMPlayer;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsInvitedChange;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.cmd.arg.ARSet;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsInviteAdd extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	public CmdFactionsInviteAdd()
	{
		// Aliases
		this.addAliases("a", "add");

		// Args
		this.addArg(ARSet.get(ARMPlayer.getAny(), true), "players", true);
		
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.INVITE_ADD.node));
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //	
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		Collection<MPlayer> mplayers = this.readArg();
		
		// MPerm
		if ( ! MPerm.getPermInvite().has(msender, msenderFaction, true)) return;
		
		for (MPlayer mplayer : mplayers)
		{	
			// Already member?
			if (mplayer.getFaction() == msenderFaction)
			{
				msg("%s<i> is already a member of %s<i>.", mplayer.getName(), msenderFaction.getName());
				continue;
			}
			
			// Already invited?
			boolean isInvited = msenderFaction.isInvited(mplayer);
			
			if ( ! isInvited)
			{
				// Event
				EventFactionsInvitedChange event = new EventFactionsInvitedChange(sender, mplayer, msenderFaction, isInvited);
				event.run();
				if (event.isCancelled()) continue;
				isInvited = event.isNewInvited();
				
				// Inform
				mplayer.msg("%s<i> invited you to %s<i>.", msender.describeTo(mplayer, true), msenderFaction.describeTo(mplayer));
				msenderFaction.msg("%s<i> invited %s<i> to your faction.", msender.describeTo(msenderFaction, true), mplayer.describeTo(msenderFaction));
				
				// Apply
				msenderFaction.setInvited(mplayer, true);
			}
			else
			{
				// Inform
				msg("%s <i>is already invited to %s<i>.", mplayer.getName(), msenderFaction.getName());
				msg("<i>You might want to: " + Factions.get().getOuterCmdFactions().cmdFactionsInvite.cmdFactionsInviteRemove.getUseageTemplate(false));
			}
		}
	}
	
}
