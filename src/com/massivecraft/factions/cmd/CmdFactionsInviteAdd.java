package com.massivecraft.factions.cmd;

import java.util.Collection;

import org.bukkit.ChatColor;

import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsInvitedChange;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.container.TypeSet;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsInviteAdd extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	public CmdFactionsInviteAdd()
	{
		// Parameters
		this.addParameter(TypeSet.get(TypeMPlayer.get()), "players", true);
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
				msenderFaction.changed();
			}
			else
			{
				// Mson
				String command = CmdFactions.get().cmdFactionsInvite.cmdFactionsInviteRemove.getCommandLine(mplayer.getName());
				String tooltip = Txt.parse("<i>Click to <c>%s<i>.", command);
				
				Mson remove = Mson.mson(
					mson("You might want to remove him. ").color(ChatColor.YELLOW), 
					mson("Click to " + command).color(ChatColor.RED).tooltip(tooltip).suggest(command)
				);
				
				// Inform
				msg("%s <i>is already invited to %s<i>.", mplayer.getName(), msenderFaction.getName());
				message(remove);
			}
		}
	}
	
}
