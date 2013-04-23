package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.event.FactionsEventOpenChange;
import com.massivecraft.mcore.cmd.arg.ARBoolean;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsOpen extends FCommand
{
	public CmdFactionsOpen()
	{
		this.addAliases("open");

		this.addOptionalArg("yes/no", "toggle");

		this.addRequirements(ReqHasPerm.get(Perm.OPEN.node));
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.OFFICER));
	}

	@Override
	public void perform()
	{
		// Args
		Boolean newOpen = this.arg(0, ARBoolean.get(), !myFaction.isOpen());
		if (newOpen == null) return;

		// Event
		FactionsEventOpenChange event = new FactionsEventOpenChange(sender, myFaction, newOpen);
		event.run();
		if (event.isCancelled()) return;
		newOpen = event.isNewOpen();

		// Apply
		myFaction.setOpen(newOpen);

		// Inform
		String descTarget = myFaction.isOpen() ? "open" : "closed";
		myFaction.msg("%s<i> changed the faction to <h>%s<i>.", fme.describeTo(myFaction, true), descTarget);
	}

}
