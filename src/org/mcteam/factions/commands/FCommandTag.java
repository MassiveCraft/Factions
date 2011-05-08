package org.mcteam.factions.commands;

import java.util.ArrayList;

import org.mcteam.factions.Conf;
import org.mcteam.factions.Faction;
import org.mcteam.factions.struct.Role;
import org.mcteam.factions.util.TextUtil;


public class FCommandTag extends FBaseCommand {
	
	public FCommandTag() {
		aliases.add("tag");
		
		requiredParameters.add("faction tag");
		
		helpDescription = "Change the faction tag";
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
			if (faction == me.getFaction()) {
				continue;
			}
			faction.sendMessage(Conf.colorSystem+"The faction "+me.getRelationColor(faction)+oldtag+Conf.colorSystem+" chainged their name to "+myFaction.getTag(faction));
		}
	}
	
}
