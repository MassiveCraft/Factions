package com.massivecraft.factions.commands;

import java.util.Set;
import java.util.Iterator;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;


public class FCommandOwnerList extends FCommand {
	
	public FCommandOwnerList() {
		aliases.add("ownerlist");

		helpDescription = "list owner(s) of this claimed land";
	}
	
	@Override
	public void perform() {
		boolean hasBypass = P.hasPermAdminBypass(fme);

		if ( ! hasBypass && ! assertHasFaction()) {
			return;
		}

		if ( ! Conf.ownedAreasEnabled) {
			fme.sendMessage("Owned areas are disabled on this server.");
			return;
		}

		Faction myFaction = fme.getFaction();
		FLocation flocation = new FLocation(fme);

		if (Board.getIdAt(flocation) != myFaction.getId()) {
			if (!hasBypass) {
				fme.sendMessage("This land is not claimed by your faction.");
				return;
			}

			myFaction = Board.getFactionAt(flocation);
			if (!myFaction.isNormal()) {
				fme.sendMessage("This land is not claimed by any faction, thus no owners.");
				return;
			}
		}

		String owners = myFaction.getOwnerListString(flocation);

		if (owners == null || owners.isEmpty()) {
			fme.sendMessage("No owners are set here; everyone in the faction has access.");
			return;
		}

		fme.sendMessage("Current owner(s) of this land: "+owners);
	}
}
