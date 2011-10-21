package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;

public class CmdUnclaimall extends FCommand
{	
	public CmdUnclaimall()
	{
		this.aliases.add("unclaimall");
		this.aliases.add("declaimall");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.UNCLAIM_ALL.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		double refund = Econ.calculateTotalLandRefund(myFaction.getLandRounded());
		if(Conf.bankFactionPaysLandCosts)
		{
			if ( ! Econ.modifyMoney(myFaction, refund, "to unclaim all faction land", "for unclaiming all faction land")) return;
		}
		else
		{
			if ( ! Econ.modifyMoney(fme      , refund, "to unclaim all faction land", "for unclaiming all faction land")) return;
		}
		
		//String moneyBack = "<i>";
		/*if (Econ.shouldBeUsed())
		{
			double refund = Econ.calculateTotalLandRefund(myFaction.getLandRounded());
			// a real refund
			if (refund > 0.0)
			{
				if(Conf.bankFactionPaysLandCosts)
				{
					Faction faction = myFaction;
					faction.addMoney(refund);
					moneyBack = " "+faction.getTag()+" <i>received a refund of <h>"+Econ.moneyString(refund)+"<i>.";
				}
				else
				{
					Econ.addMoney(fme.getName(), refund);
					moneyBack = " They received a refund of <h>"+Econ.moneyString(refund)+"<i>.";
				}
			}
			// wait, you're charging people to unclaim land? outrageous
			else if (refund < 0.0)
			{
				if(Conf.bankFactionPaysLandCosts)
				{
					Faction faction = myFaction;
					if(!faction.removeMoney(-refund))
					{
						msg("<b>Unclaiming all faction land will cost <h>"+Econ.moneyString(-refund)+"<b>, which your faction can't currently afford.");
						return;
					}
					moneyBack = " It cost "+faction.getTag()+" "+Econ.moneyString(refund)+".";
				}
				else
				{
					if (!Econ.deductMoney(fme.getName(), -refund))
					{
						msg("<b>Unclaiming all faction land will cost <h>"+Econ.moneyString(-refund)+"<b>, which you can't currently afford.");
						return;
					}
					moneyBack = "<i> It cost them <h>"+Econ.moneyString(refund)+"<i>.";
				}
				moneyBack = "<i> It cost them <h>"+Econ.moneyString(refund)+"<i>.";
			}
			// no refund
			else
			{
				moneyBack = "";
			}
		}*/

		Board.unclaimAll(myFaction.getId());
		myFaction.msg("%s<i> unclaimed ALL of your faction's land.", fme.describeTo(myFaction, true));
	}
	
}
