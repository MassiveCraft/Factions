package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Role;

public class FCommandOpen extends FBaseCommand {
	
	public FCommandOpen() {
		aliases.add("open");
		aliases.add("close");
		
		helpDescription = "Switch if invitation is required to join";
	}
	
	@Override
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if( isLocked() ) {
			sendLockMessage();
			return;
		}
		
		if ( ! assertMinRole(Role.MODERATOR)) {
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostOpen)) {
			return;
		}

		Faction myFaction = me.getFaction();
		myFaction.setOpen( ! me.getFaction().getOpen());
		
		String open = myFaction.getOpen() ? "open" : "closed";
		
		// Inform
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" changed the faction to "+open);
		for (Faction faction : Faction.getAll()) {
			if (faction == me.getFaction()) {
				continue;
			}
			faction.sendMessage(Conf.colorSystem+"The faction "+myFaction.getTag(faction)+Conf.colorSystem+" is now "+open);
		}
	}
	
}
