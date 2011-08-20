package com.massivecraft.factions.commands;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.SpoutFeatures;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;


public class FRelationCommand extends FBaseCommand {
	
	public FRelationCommand() {
		requiredParameters.add("faction tag");
		
		helpDescription = "Set relation wish to another faction";
	}
	
	public void relation(Relation whishedRelation, String otherFactionName) {
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
		
		Faction myFaction = me.getFaction();
		Faction otherFaction = findFaction(otherFactionName, false);
		if (otherFaction == null) {
			return;
		}
		
		if (otherFaction.getId() == 0) {
			sendMessage("Nope! You can't :) The default faction is not a real faction.");
			return;
		}
		
		if (otherFaction == myFaction) {
			sendMessage("Nope! You can't declare a relation to yourself :)");
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		double cost = whishedRelation.isAlly() ? Conf.econCostAlly : (whishedRelation.isEnemy() ? Conf.econCostEnemy : Conf.econCostNeutral);
		if (!payForCommand(cost)) {
			return;
		}

		myFaction.setRelationWish(otherFaction, whishedRelation);
		Relation currentRelation = myFaction.getRelation(otherFaction, true);
		ChatColor currentRelationColor = currentRelation.getColor();
		if (whishedRelation.value == currentRelation.value) {
			otherFaction.sendMessage(Conf.colorSystem+"Your faction is now "+currentRelationColor+whishedRelation.toString()+Conf.colorSystem+" to "+currentRelationColor+myFaction.getTag());
			myFaction.sendMessage(Conf.colorSystem+"Your faction is now "+currentRelationColor+whishedRelation.toString()+Conf.colorSystem+" to "+currentRelationColor+otherFaction.getTag());
		} else {
			otherFaction.sendMessage(currentRelationColor+myFaction.getTag()+Conf.colorSystem+ " wishes to be your "+whishedRelation.getColor()+whishedRelation.toString());
			otherFaction.sendMessage(Conf.colorSystem+"Type "+Conf.colorCommand+Factions.instance.getBaseCommand()+" "+whishedRelation+" "+myFaction.getTag()+Conf.colorSystem+" to accept.");
			myFaction.sendMessage(currentRelationColor+otherFaction.getTag()+Conf.colorSystem+ " were informed that you wish to be "+whishedRelation.getColor()+whishedRelation);
		}
		if (!whishedRelation.isNeutral() && otherFaction.isPeaceful()) {
			otherFaction.sendMessage(Conf.colorSystem+"This will have no effect while your faction is peaceful.");
			myFaction.sendMessage(Conf.colorSystem+"This will have no effect while their faction is peaceful.");
		}
		if (!whishedRelation.isNeutral() && myFaction.isPeaceful()) {
			otherFaction.sendMessage(Conf.colorSystem+"This will have no effect while their faction is peaceful.");
			myFaction.sendMessage(Conf.colorSystem+"This will have no effect while your faction is peaceful.");
		}

		SpoutFeatures.updateAppearances(myFaction, otherFaction);
	}
}
