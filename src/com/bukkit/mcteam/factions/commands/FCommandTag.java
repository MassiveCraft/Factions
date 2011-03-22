package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.struct.Role;
import com.bukkit.mcteam.factions.util.TextUtil;

public class FCommandTag extends FBaseCommand {
	
	public FCommandTag() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		requiredParameters.add("faction tag");
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Change the faction tag";
	}
	
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if ( ! assertMinRole(Role.MODERATOR)) {
			return;
		}
		
		String tag = parameters.get(0);
		
		// TODO does not first test cover selfcase?
		if (Faction.isTagTaken(tag) && ! TextUtil.getComparisonString(tag).equals(me.getFaction().getComparisonTag())) {
			sendMessage("That tag is already taken");
			return;
		}
		
		ArrayList<String> errors = new ArrayList<String>();
		errors.addAll(Faction.validateTag(tag));
		if (errors.size() > 0) {
			sendMessage(errors);
			return;
		}

		Faction myFaction = me.getFaction();
		
		String oldtag = myFaction.getTag();
		myFaction.setTag(tag);
		
		// Inform
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" changed your faction tag to "+Conf.colorMember+myFaction.getTag());
		for (Faction faction : Faction.getAll()) {
			if (faction.id == me.factionId) {
				continue;
			}
			faction.sendMessage(Conf.colorSystem+"The faction "+me.getRelationColor(faction)+oldtag+Conf.colorSystem+" chainged their name to "+myFaction.getTag(faction));
		}
	}
	
}
