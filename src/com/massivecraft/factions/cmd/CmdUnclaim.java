package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
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
		
		this.permission = Permission.UNCLAIM.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		FLocation flocation = new FLocation(fme);
		Faction otherFaction = Board.getFactionAt(flocation);
		
		if (otherFaction.isSafeZone())
		{
			if (Permission.MANAGE_SAFE_ZONE.has(sender))
			{
				Board.removeAt(flocation);
				SpoutFeatures.updateTerritoryDisplayLoc(flocation);
				msg("<i>Safe zone was unclaimed.");

				if (Conf.logLandUnclaims)
					P.p.log(fme.getName()+" unclaimed land at ("+flocation.getCoordString()+") from the faction: "+otherFaction.getTag());
			}
			else
			{
				msg("<b>This is a safe zone. You lack permissions to unclaim.");
			}
			return;
		}
		else if (otherFaction.isWarZone())
		{
			if (Permission.MANAGE_WAR_ZONE.has(sender))
			{
				Board.removeAt(flocation);
				SpoutFeatures.updateTerritoryDisplayLoc(flocation);
				msg("<i>War zone was unclaimed.");

				if (Conf.logLandUnclaims)
					P.p.log(fme.getName()+" unclaimed land at ("+flocation.getCoordString()+") from the faction: "+otherFaction.getTag());
			}
			else
			{
				msg("<b>This is a war zone. You lack permissions to unclaim.");
			}
			return;
		}
		
		if (fme.isAdminBypassing())
		{
			Board.removeAt(flocation);
			SpoutFeatures.updateTerritoryDisplayLoc(flocation);

			otherFaction.msg("%s<i> unclaimed some of your land.", fme.describeTo(otherFaction, true));
			msg("<i>You unclaimed this land.");

			if (Conf.logLandUnclaims)
				P.p.log(fme.getName()+" unclaimed land at ("+flocation.getCoordString()+") from the faction: "+otherFaction.getTag());

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
			msg("<b>You don't own this land.");
			return;
		}

		//String moneyBack = "<i>";
		if (Econ.shouldBeUsed())
		{
			double refund = Econ.calculateClaimRefund(myFaction.getLandRounded());
			
			if(Conf.bankFactionPaysLandCosts)
			{
				if ( ! Econ.modifyMoney(myFaction, refund, "to unclaim this land", "for unclaiming this land")) return;
			}
			else
			{
				if ( ! Econ.modifyMoney(fme      , refund, "to unclaim this land", "for unclaiming this land")) return;
			}
			
			/*
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
						msg("<b>Unclaiming this land will cost <h>%s<b> which your faction can't currently afford.", Econ.moneyString(-refund));
						return;
					}
					moneyBack = " It cost "+faction.getTag()+" <h>"+Econ.moneyString(refund)+"<i>.";
				}
				else
				{
					if (!Econ.deductMoney(fme.getName(), -refund))
					{
						msg("<b>Unclaiming this land will cost <h>%s<b> which you can't currently afford.", Econ.moneyString(-refund));
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
			*/
		}

		Board.removeAt(flocation);
		SpoutFeatures.updateTerritoryDisplayLoc(flocation);
		myFaction.msg("%s<i> unclaimed some land.", fme.describeTo(myFaction, true));

		if (Conf.logLandUnclaims)
			P.p.log(fme.getName()+" unclaimed land at ("+flocation.getCoordString()+") from the faction: "+otherFaction.getTag());
	}
	
}
