package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.SpoutFeatures;
import com.massivecraft.factions.util.TextUtil;

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

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostTitle)) {
			return;
		}

		you.setTitle(title);
		
		// Inform
		Faction myFaction = me.getFaction();
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" changed a title: "+you.getNameAndRelevant(myFaction));

		if (Conf.spoutFactionTitlesOverNames) {
			SpoutFeatures.updateAppearances(player);
		}
	}
	
}
