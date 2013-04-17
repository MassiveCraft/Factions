package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsDescription extends FCommand
{
	public CmdFactionsDescription()
	{
		this.addAliases("desc");
		
		this.addRequiredArg("desc");
		this.setErrorOnToManyArgs(false);
		
		this.addRequirements(ReqHasPerm.get(Perm.DESCRIPTION.node));
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.OFFICER));
	}
	
	@Override
	public void perform()
	{
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(ConfServer.econCostDesc, "to change faction description", "for changing faction description")) return;

		if (ConfServer.broadcastDescriptionChanges)
		{
			// Broadcast the description to everyone
			for (FPlayer fplayer : FPlayerColl.get().getAllOnline())
			{
				fplayer.msg("<h>%s<i> changed their description to:", myFaction.describeTo(fplayer));
				fplayer.sendMessage(myFaction.getDescription());
			}
		}
		else
		{
			fme.msg("You have changed the description for <h>%s<i> to:", myFaction.describeTo(fme));
			fme.sendMessage(myFaction.getDescription());
		}
		
	}
	
}
