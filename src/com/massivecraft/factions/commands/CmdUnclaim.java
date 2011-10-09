package com.massivecraft.factions.commands;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class CmdUnclaim extends FCommand
{
	public CmdUnclaim()
	{
		this.aliases.add("unclaim");
		this.aliases.add("declaim");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.COMMAND_UNCLAIM.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
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
		
		FLocation flocation = new FLocation(fme);
		Faction otherFaction = Board.getFactionAt(flocation);
		
		if (otherFaction.isSafeZone())
		{
			if (Permission.MANAGE_SAFE_ZONE.has(sender))
			{
				Board.removeAt(flocation);
				sendMessageParsed("<i>Safe zone was unclaimed.");
			}
			else
			{
				sendMessageParsed("<b>This is a safe zone. You lack permissions to unclaim.");
			}
			return;
		}
		else if (otherFaction.isWarZone())
		{
			if (Permission.MANAGE_WAR_ZONE.has(sender))
			{
				Board.removeAt(flocation);
				sendMessageParsed("<i>War zone was unclaimed.");
			}
			else
			{
				sendMessageParsed("<b>This is a war zone. You lack permissions to unclaim.");
			}
			return;
		}
		
		if (fme.isAdminBypassing())
		{
			Board.removeAt(flocation);

			otherFaction.sendMessageParsed("%s<i> unclaimed some of your land.", fme.getNameAndRelevant(otherFaction));
			sendMessageParsed("<i>You unclaimed this land.");
			return;
		}
		
		if ( ! assertHasFaction())
		{
			return;
		}
		
		if ( ! assertMinRole(Role.MODERATOR))
		{
			return;
		}
		
		
		if ( myFaction != otherFaction)
		{
			sendMessageParsed("<b>You don't own this land.");
			return;
		}

		String moneyBack = "<i>";
		if (Econ.enabled())
		{
			double refund = Econ.calculateClaimRefund(myFaction.getLandRounded());
			// a real refund
			if (refund > 0.0)
			{
				if(Conf.bankFactionPaysLandCosts)
				{
					Faction faction = myFaction;
					faction.addMoney(refund);
					moneyBack = " "+faction.getTag()+"<i> received a refund of <h>"+Econ.moneyString(refund)+"<i>.";
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
						sendMessageParsed("<b>Unclaiming this land will cost <h>%s<b> which your faction can't currently afford.", Econ.moneyString(-refund));
						return;
					}
					moneyBack = " It cost "+faction.getTag()+" <h>"+Econ.moneyString(refund)+"<i>.";
				}
				else
				{
					if (!Econ.deductMoney(fme.getName(), -refund))
					{
						sendMessageParsed("<b>Unclaiming this land will cost <h>%s<b> which you can't currently afford.", Econ.moneyString(-refund));
						return;
					}
					moneyBack = " It cost them <h>"+Econ.moneyString(refund)+"<i>.";
				}
			}
			// no refund
			else
			{
				moneyBack = "";
			}
		}

		Board.removeAt(flocation);
		myFaction.sendMessageParsed("%s<i> unclaimed some land."+moneyBack, fme.getNameAndRelevant(myFaction));
	}
	
}
