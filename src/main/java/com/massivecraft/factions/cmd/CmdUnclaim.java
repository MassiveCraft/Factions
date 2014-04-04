package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.event.LandUnclaimEvent;
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

		LandUnclaimEvent unclaimEvent = new LandUnclaimEvent(flocation, otherFaction, fme);
		Bukkit.getServer().getPluginManager().callEvent(unclaimEvent);
		if(unclaimEvent.isCancelled()) return;

		if (Econ.shouldBeUsed())
		{
			double refund = Econ.calculateClaimRefund(myFaction.getLandRounded());
			
			if(Conf.bankEnabled && Conf.bankFactionPaysLandCosts)
			{
				if ( ! Econ.modifyMoney(myFaction, refund, "to unclaim this land", "for unclaiming this land")) return;
			}
			else
			{
				if ( ! Econ.modifyMoney(fme      , refund, "to unclaim this land", "for unclaiming this land")) return;
			}
		}

		Board.removeAt(flocation);
		SpoutFeatures.updateTerritoryDisplayLoc(flocation);
		myFaction.msg("%s<i> unclaimed some land.", fme.describeTo(myFaction, true));

		if (Conf.logLandUnclaims)
			P.p.log(fme.getName()+" unclaimed land at ("+flocation.getCoordString()+") from the faction: "+otherFaction.getTag());
	}
	
}
