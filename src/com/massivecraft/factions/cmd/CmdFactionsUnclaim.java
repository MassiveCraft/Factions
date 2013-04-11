package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.BoardOld;
import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;

public class CmdFactionsUnclaim extends FCommand
{
	public CmdFactionsUnclaim()
	{
		this.aliases.add("unclaim");
		this.aliases.add("declaim");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Perm.UNCLAIM.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		FLocation flocation = new FLocation(fme);
		Faction otherFaction = BoardOld.getFactionAt(flocation);

		if ( ! FPerm.TERRITORY.has(sender, otherFaction, true)) return;

		LandUnclaimEvent unclaimEvent = new LandUnclaimEvent(flocation, otherFaction, fme);
		Bukkit.getServer().getPluginManager().callEvent(unclaimEvent);
		if(unclaimEvent.isCancelled()) return;
	
		//String moneyBack = "<i>";
		if (Econ.shouldBeUsed())
		{
			double refund = Econ.calculateClaimRefund(myFaction.getLandRounded());
			
			if(ConfServer.bankEnabled && ConfServer.bankFactionPaysLandCosts)
			{
				if ( ! Econ.modifyMoney(myFaction, refund, "to unclaim this land", "for unclaiming this land")) return;
			}
			else
			{
				if ( ! Econ.modifyMoney(fme      , refund, "to unclaim this land", "for unclaiming this land")) return;
			}
		}

		BoardOld.removeAt(flocation);
		SpoutFeatures.updateTerritoryDisplayLoc(flocation);
		myFaction.msg("%s<i> unclaimed some land.", fme.describeTo(myFaction, true));

		if (ConfServer.logLandUnclaims)
			Factions.get().log(fme.getName()+" unclaimed land at ("+flocation.getCoordString()+") from the faction: "+otherFaction.getTag());
	}
	
}
