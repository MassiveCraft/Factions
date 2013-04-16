package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARFPlayer;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.mcore.cmd.arg.ARString;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsTitle extends FCommand
{
	public CmdFactionsTitle()
	{
		this.addAliases("title");
		
		this.addRequiredArg("player");
		this.addOptionalArg("title", "");
		
		this.addRequirements(ReqHasPerm.get(Perm.TITLE.node));
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.OFFICER));
	}
	
	@Override
	public void perform()
	{
		FPlayer you = this.arg(0, ARFPlayer.getStartAny());
		if (you == null) return;
		
		String title = this.argConcatFrom(1, ARString.get(), "");
		if (title == null) return;
		
		if ( ! canIAdministerYou(fme, you)) return;

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(ConfServer.econCostTitle, "to change a players title", "for changing a players title")) return;

		you.setTitle(title);
		
		// Inform
		myFaction.msg("%s<i> changed a title: %s", fme.describeTo(myFaction, true), you.describeTo(myFaction, true));

		if (ConfServer.spoutFactionTitlesOverNames)
		{
			SpoutFeatures.updateTitle(me, null);
		}
	}
	
}
