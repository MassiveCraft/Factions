package org.mcteam.factions.commands;

import org.mcteam.factions.Conf;
import org.mcteam.factions.FPlayer;
import org.mcteam.factions.Faction;
import org.mcteam.factions.util.TextUtil;

public class FCommandTitle extends FBaseCommand {
	
	public FCommandTitle() {
		aliases.add("title");
		
		requiredParameters.add("player name");
		
		optionalParameters.add("title");
		
		helpDescription = "Set or remove a players title";
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
		
		String playerName = parameters.get(0);
		parameters.remove(0);
		String title = TextUtil.implode(parameters);
		
		FPlayer you = findFPlayer(playerName, false);
		if (you == null) {
			return;
		}
		
		if ( ! canIAdministerYou(me, you)) {
			return;
		}
		
		you.setTitle(title);
		
		// Inform
		Faction myFaction = me.getFaction();
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" changed a title: "+you.getNameAndRelevant(myFaction));
	}
	
}
