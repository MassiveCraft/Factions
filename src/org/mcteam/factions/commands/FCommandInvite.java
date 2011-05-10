package org.mcteam.factions.commands;

import org.mcteam.factions.Conf;
import org.mcteam.factions.FPlayer;
import org.mcteam.factions.Faction;
import org.mcteam.factions.struct.Role;

public class FCommandInvite extends FBaseCommand {
	
	public FCommandInvite() {
		aliases.add("invite");
		aliases.add("inv");
		
		requiredParameters.add("player name");
		
		helpDescription = "Invite a player";
	}
	
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
		
		String playerName = parameters.get(0);
		
		FPlayer you = findFPlayer(playerName, false);
		if (you == null) {
			return;
		}
		
		Faction myFaction = me.getFaction();
		
		if (you.getFaction() == myFaction) {
			sendMessage(you.getName()+" is already a member of "+myFaction.getTag());
			sendMessage("You might want to: " + new FCommandKick().getUseageTemplate(false));
			return;
		}
		
		myFaction.invite(you);
		
		you.sendMessage(me.getNameAndRelevant(you)+Conf.colorSystem+" invited you to "+myFaction.getTag(you));
		myFaction.sendMessage(me.getNameAndRelevant(me)+Conf.colorSystem+" invited "+you.getNameAndRelevant(me)+Conf.colorSystem+" to your faction.");
	}
	
}
