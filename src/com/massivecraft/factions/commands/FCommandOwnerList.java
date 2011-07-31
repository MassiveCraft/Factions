package com.massivecraft.factions.commands;

import java.util.Set;
import java.util.Iterator;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;


public class FCommandOwnerList extends FBaseCommand {
	
	public FCommandOwnerList() {
		aliases.add("ownerlist");

		helpDescription = "list owner(s) of this claimed land";
	}
	
	@Override
	public void perform() {
		boolean hasBypass = Factions.hasPermAdminBypass(player);

		if ( ! hasBypass && ! assertHasFaction()) {
			return;
		}

		if ( ! Conf.ownedAreasEnabled) {
			me.sendMessage("Owned areas are disabled on this server.");
			return;
		}

		Faction myFaction = me.getFaction();
		FLocation flocation = new FLocation(me);

		if (Board.getIdAt(flocation) != myFaction.getId()) {
			if (!hasBypass) {
				me.sendMessage("This land is not claimed by your faction.");
				return;
			}

			myFaction = Board.getFactionAt(flocation);
			if (!myFaction.isNormal()) {
				me.sendMessage("This land is not claimed by any faction, thus no owners.");
				return;
			}
		}

		String owners = myFaction.getOwnerListString(flocation);

		if (owners == null || owners.isEmpty()) {
			me.sendMessage("No owners are set here; everyone in the faction has access.");
			return;
		}

		me.sendMessage("Current owner(s) of this land: "+owners);
	}
}
