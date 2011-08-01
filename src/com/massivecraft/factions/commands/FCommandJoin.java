package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;

public class FCommandJoin extends FBaseCommand {
	
	public FCommandJoin() {
		aliases.add("join");
		
		requiredParameters.add("faction name");
		
		helpDescription = "Join a faction";
	}
	
	@Override
	public void perform() {
		
		if( isLocked() ) {
			sendLockMessage();
			return;
		}
		
		String factionName = parameters.get(0);
		
		Faction faction = findFaction(factionName);
		if (faction == null) {
			return;
		}

		if ( ! faction.isNormal()) {
			sendMessage("You may only join normal factions. This is a system faction.");
			return;
		}
		
		if (faction == me.getFaction()) {
			sendMessage("You are already a member of "+faction.getTag(me));
			return;
		}
		
		if (me.hasFaction()) {
			sendMessage("You must leave your current faction first.");
			return;
		}
		
		if (!Conf.CanLeaveWithNegativePower && me.getPower() < 0) {
			sendMessage("You cannot join a faction until your power is positive.");
			return;
		}
		
		if( ! faction.getOpen() && ! faction.isInvited(me)) {
			sendMessage("This guild requires invitation.");
			faction.sendMessage(me.getNameAndRelevant(faction)+Conf.colorSystem+" tried to join your faction.");
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostJoin)) {
			return;
		}

		me.sendMessage(Conf.colorSystem+"You successfully joined "+faction.getTag(me));
		faction.sendMessage(me.getNameAndRelevant(faction)+Conf.colorSystem+" joined your faction.");
		
		me.resetFactionData();
		me.setFaction(faction);
		faction.deinvite(me);
	}
	
}
