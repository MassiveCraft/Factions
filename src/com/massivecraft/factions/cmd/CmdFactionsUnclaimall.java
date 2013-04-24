package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.event.FactionsEventLandUnclaimAll;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsUnclaimall extends FCommand
{	
	public CmdFactionsUnclaimall()
	{
		this.addAliases("unclaimall", "declaimall");
		
		this.addRequirements(ReqHasPerm.get(Perm.UNCLAIM_ALL.node));
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.OFFICER));
	}
	
	@Override
	public void perform()
	{
		// TODO: Put this as a listener and not in here!
		if (Econ.isEnabled(myFaction))
		{
			double refund = Econ.calculateTotalLandRefund(myFaction.getLandCount());
			
			if (UConf.get(myFaction).bankEnabled && UConf.get(myFaction).bankFactionPaysLandCosts)
			{
				if ( ! Econ.modifyMoney(myFaction, refund, "unclaim all faction land")) return;
			}
			else
			{
				if ( ! Econ.modifyMoney(fme, refund, "unclaim all faction land")) return;
			}
		}

		// Event
		FactionsEventLandUnclaimAll event = new FactionsEventLandUnclaimAll(sender, myFaction);
		event.run();
		// TODO: this event cannot be cancelled yet.

		// Apply
		BoardColls.get().removeAll(myFaction);
		
		// Inform
		myFaction.msg("%s<i> unclaimed ALL of your faction's land.", fme.describeTo(myFaction, true));

		if (MConf.get().logLandUnclaims)
		{
			Factions.get().log(fme.getName()+" unclaimed everything for the faction: "+myFaction.getTag());
		}
	}
	
}
