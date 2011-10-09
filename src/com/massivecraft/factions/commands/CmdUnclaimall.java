package com.massivecraft.factions.commands;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;

public class CmdUnclaimall extends FCommand
{	
	public CmdUnclaimall()
	{
		this.aliases.add("unclaimall");
		this.aliases.add("declaimall");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.COMMAND_UNCLAIM_ALL.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		if( isLocked() )
		{
			sendLockMessage();
			return;
		}

		String moneyBack = "<i>";
		if (Econ.enabled())
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
						sendMessageParsed("<b>Unclaiming all faction land will cost <h>"+Econ.moneyString(-refund)+"<b>, which your faction can't currently afford.");
						return;
					}
					moneyBack = " It cost "+faction.getTag()+" "+Econ.moneyString(refund)+".";
				}
				else
				{
					if (!Econ.deductMoney(fme.getName(), -refund))
					{
						sendMessageParsed("<b>Unclaiming all faction land will cost <h>"+Econ.moneyString(-refund)+"<b>, which you can't currently afford.");
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
		}

		Board.unclaimAll(myFaction.getId());
		myFaction.sendMessageParsed("%s<i> unclaimed ALL of your faction's land."+moneyBack, fme.getNameAndRelevant(myFaction));
	}
	
}
