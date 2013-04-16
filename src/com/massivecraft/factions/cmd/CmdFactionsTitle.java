package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.util.Txt;

public class CmdFactionsTitle extends FCommand
{
	public CmdFactionsTitle()
	{
		this.addAliases("title");
		
		this.requiredArgs.add("player");
		this.optionalArgs.put("title", "");
		
		this.addRequirements(ReqHasPerm.get(Perm.TITLE.node));
		
		senderMustBeOfficer = true;
	}
	
	@Override
	public void perform()
	{
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;
		
		args.remove(0);
		String title = Txt.implode(args, " ");
		
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
