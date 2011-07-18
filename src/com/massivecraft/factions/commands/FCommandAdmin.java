package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Role;

public class FCommandAdmin extends FBaseCommand {
	
	public FCommandAdmin() {
		aliases.add("admin");
		
		requiredParameters.add("player name");
		
		helpDescription = "Hand over your admin rights";
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
		
		if ( ! assertMinRole(Role.ADMIN)) {
			return;
		}
		
		String playerName = parameters.get(0);
		
		FPlayer you = findFPlayer(playerName, false);
		if (you == null) {
			return;
		}
		
		Faction myFaction = me.getFaction();
		
		if (you.getFaction() != myFaction) {
			sendMessage(you.getNameAndRelevant(me)+Conf.colorSystem+" is not a member in your faction.");
			return;
		}
		
		if (you == me) {
			sendMessage("The target player musn't be yourself.");
			return;
		}

		
		me.setRole(Role.MODERATOR);
		you.setRole(Role.ADMIN);
		
		// Inform all players
		for (FPlayer fplayer : FPlayer.getAllOnline()) {
			if (fplayer.getFaction() == me.getFaction()) {
				fplayer.sendMessage(me.getNameAndRelevant(me)+Conf.colorSystem+" gave "+you.getNameAndRelevant(me)+Conf.colorSystem+" the leadership of your faction.");
			} else {
				fplayer.sendMessage(me.getNameAndRelevant(fplayer)+Conf.colorSystem+" gave "+you.getNameAndRelevant(fplayer)+Conf.colorSystem+" the leadership of "+myFaction.getTag(fplayer));
			}
		}
	}
	
}
