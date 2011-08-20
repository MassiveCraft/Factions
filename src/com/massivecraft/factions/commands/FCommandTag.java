package com.massivecraft.factions.commands;

import java.util.ArrayList;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.SpoutFeatures;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.TextUtil;


public class FCommandTag extends FBaseCommand {
	
	public FCommandTag() {
		aliases.add("tag");
		
		requiredParameters.add("faction tag");
		
		helpDescription = "Change the faction tag";
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

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostTag)) {
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
			faction.sendMessage(Conf.colorSystem+"The faction "+me.getRelationColor(faction)+oldtag+Conf.colorSystem+" changed their name to "+myFaction.getTag(faction));
		}

		if (Conf.spoutFactionTagsOverNames) {
			SpoutFeatures.updateAppearances(myFaction);
		}
	}
	
}
