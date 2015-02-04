package com.massivecraft.factions.cmd;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARMPlayer;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsInvitedChange;
import com.massivecraft.massivecore.cmd.MassiveCommandException;
import com.massivecraft.massivecore.cmd.arg.ARSet;
import com.massivecraft.massivecore.cmd.arg.ARString;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsInviteRemove extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	public CmdFactionsInviteRemove()
	{
		// Aliases
		this.addAliases("r", "remove");

		// Args
		this.addRequiredArg("players/all");
		this.setErrorOnToManyArgs(false);
		
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.INVITE_REMOVE.node));
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //	
	
	@Override
	public void perform() throws MassiveCommandException
	{
		Set<MPlayer> mplayers = new HashSet<MPlayer>();
		boolean all = false;
		
		// Args
		if (this.arg(0, ARString.get()).equalsIgnoreCase("all"))
		{
			List<MPlayer> invitedPlayers = msenderFaction.getInvitedMPlayers();
			// Doesn't show up if list is empty. Test at home if it worked.
			if (invitedPlayers == null || invitedPlayers.isEmpty())
			{
				msg("<b>Your faction has not invited anyone.");
				return;
			}
			all = true;
			mplayers.addAll(invitedPlayers);
		}
		else
		{
			Set<MPlayer> senderInput = this.argConcatFrom(0, ARSet.get(ARMPlayer.getAny(), true));
			
			mplayers.addAll(senderInput);
		}
		
		// MPerm
		if ( ! MPerm.getPermInvite().has(msender, msenderFaction, true)) return;
		
		for (MPlayer mplayer : mplayers)
		{
			// Already member?
			if (mplayer.getFaction() == msenderFaction)
			{
				msg("%s<i> is already a member of %s<i>.", mplayer.getName(), msenderFaction.getName());
				msg("<i>You might want to: " + Factions.get().getOuterCmdFactions().cmdFactionsKick.getUseageTemplate(false));
				continue;
			}
			
			// Already invited?
			boolean isInvited = msenderFaction.isInvited(mplayer);
			
			if (isInvited)
			{
				// Event
				EventFactionsInvitedChange event = new EventFactionsInvitedChange(sender, mplayer, msenderFaction, isInvited);
				event.run();
				if (event.isCancelled()) continue;
				isInvited = event.isNewInvited();
				
				// Inform Player
				mplayer.msg("%s<i> revoked your invitation to <h>%s<i>.", msender.describeTo(mplayer, true), msenderFaction.describeTo(mplayer));
				
				// Inform Faction
				if ( ! all)
				{
					msenderFaction.msg("%s<i> revoked %s's<i> invitation.", msender.describeTo(msenderFaction), mplayer.describeTo(msenderFaction));
				}
				
				// Apply
				msenderFaction.setInvited(mplayer, false);
			}
			else
			{
				// Inform
				msg("%s <i>is not invited to %s<i>.", mplayer.describeTo(msender, true), msenderFaction.describeTo(mplayer));
				msg("<i>You might want to: " + Factions.get().getOuterCmdFactions().cmdFactionsInvite.cmdFactionsInviteAdd.getUseageTemplate(false));
			}
		}
		
		// Inform Faction if all
		if (all)
		{
			msenderFaction.msg("%s<i> revoked all <h>%s <i>pending invitations from your faction.", msender.describeTo(msenderFaction), mplayers.size());
		}
	}
	
}
