package org.mcteam.factions.commands;

import org.mcteam.factions.Conf;
import org.mcteam.factions.FPlayer;
import org.mcteam.factions.Faction;

public class FCommandKick extends FBaseCommand {
	
	public FCommandKick() {
		aliases.add("kick");
		
		requiredParameters.add("player name");
		
		helpDescription = "Kick a player from the faction";
	}
	
	@Override
	public void perform() {
		
		if( isLocked() ) {
			sendLockMessage();
			return;
		}
		
		String playerName = parameters.get(0);
		
		FPlayer you = findFPlayer(playerName, false);
		if (you == null) {
			return;
		}
		
		Faction myFaction = me.getFaction();

		if (you.getFaction() != myFaction) {
			sendMessage(you.getNameAndRelevant(me)+Conf.colorSystem+" is not a member of "+myFaction.getTag(me));
			return;
		}
		
		if (me == you) {
			sendMessage("You cannot kick yourself.");
			sendMessage("You might want to: " + new FCommandLeave().getUseageTemplate(false));
			return;
		}
		
		if (you.getRole().value >= me.getRole().value) { // TODO add more informative messages.
			sendMessage("Your rank is too low to kick this player.");
			return;
		}
		
		if (!Conf.CanLeaveWithNegativePower && you.getPower() < 0) {
			sendMessage("You cannot kick that member until their power is positive.");
			return;
		}
		
		myFaction.deinvite(you);
		you.resetFactionData();
		
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" kicked "+you.getNameAndRelevant(myFaction)+Conf.colorSystem+" from the faction! :O");
		you.sendMessage(me.getNameAndRelevant(you)+Conf.colorSystem+" kicked you from "+myFaction.getTag(you)+Conf.colorSystem+"! :O");
	}
	
}
