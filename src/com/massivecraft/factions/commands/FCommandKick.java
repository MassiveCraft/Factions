package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

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
		
		if (me == you) {
			sendMessage("You cannot kick yourself.");
			sendMessage("You might want to: " + new FCommandLeave().getUseageTemplate(false));
			return;
		}

		Faction yourFaction = you.getFaction();
		Faction myFaction = me.getFaction();

		// players with admin-level "disband" permission can bypass these requirements
		if (!Factions.hasPermDisband(sender)) {
			if (yourFaction != myFaction) {
				sendMessage(you.getNameAndRelevant(me)+Conf.colorSystem+" is not a member of "+myFaction.getTag(me));
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
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostKick)) {
			return;
		}

		yourFaction.sendMessage(me.getNameAndRelevant(yourFaction)+Conf.colorSystem+" kicked "+you.getNameAndRelevant(yourFaction)+Conf.colorSystem+" from the faction! :O");
		you.sendMessage(me.getNameAndRelevant(you)+Conf.colorSystem+" kicked you from "+yourFaction.getTag(you)+Conf.colorSystem+"! :O");
		if (yourFaction != myFaction) {
			me.sendMessage(Conf.colorSystem+"You kicked "+you.getNameAndRelevant(myFaction)+Conf.colorSystem+" from the faction "+yourFaction.getTag(me)+Conf.colorSystem+"!");
		}

		yourFaction.deinvite(you);
		you.resetFactionData();

		if (yourFaction.getFPlayers().isEmpty() && !yourFaction.isPermanent()) {
			// Remove this faction
			for (FPlayer fplayer : FPlayer.getAllOnline()) {
				fplayer.sendMessage("The faction "+yourFaction.getTag(fplayer)+Conf.colorSystem+" was disbanded.");
			}
			Faction.delete(yourFaction.getId());
		}
	}
	
}
