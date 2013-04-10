package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionColl;
import com.massivecraft.factions.Perm;

public class CmdFactionsOpen extends FCommand
{
	public CmdFactionsOpen()
	{
		super();
		this.aliases.add("open");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("yes/no", "flip");
		
		this.permission = Perm.OPEN.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = true;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(ConfServer.econCostOpen, "to open or close the faction", "for opening or closing the faction")) return;

		myFaction.setOpen(this.argAsBool(0, ! myFaction.getOpen()));
		
		String open = myFaction.getOpen() ? "open" : "closed";
		
		// Inform
		myFaction.msg("%s<i> changed the faction to <h>%s<i>.", fme.describeTo(myFaction, true), open);
		for (Faction faction : FactionColl.i.get())
		{
			if (faction == myFaction)
			{
				continue;
			}
			faction.msg("<i>The faction %s<i> is now %s", myFaction.getTag(faction), open);
		}
	}
	
}
