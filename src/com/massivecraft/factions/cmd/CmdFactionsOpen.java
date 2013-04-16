package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionColl;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsOpen extends FCommand
{
	public CmdFactionsOpen()
	{
		super();
		
		this.addAliases("open");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("yes/no", "flip");
		
		this.addRequirements(ReqHasPerm.get(Perm.OPEN.node));
		
		senderMustBeOfficer = true;
	}
	
	@Override
	public void perform()
	{
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(ConfServer.econCostOpen, "to open or close the faction", "for opening or closing the faction")) return;

		myFaction.setOpen(this.argAsBool(0, ! myFaction.isOpen()));
		
		String open = myFaction.isOpen() ? "open" : "closed";
		
		// Inform
		myFaction.msg("%s<i> changed the faction to <h>%s<i>.", fme.describeTo(myFaction, true), open);
		for (Faction faction : FactionColl.get().getAll())
		{
			if (faction == myFaction)
			{
				continue;
			}
			faction.msg("<i>The faction %s<i> is now %s", myFaction.getTag(faction), open);
		}
	}
	
}
