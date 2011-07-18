package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Role;

public class FCommandDeinvite extends FBaseCommand {
	
	public FCommandDeinvite() {
		aliases.add("deinvite");
		aliases.add("deinv");
		
		requiredParameters.add("player name");
		
		helpDescription = "Remove a pending invitation";
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
		
		FPlayer you = findFPlayer(playerName, false);
		if (you == null) {
			return;
		}
		
		Faction myFaction = me.getFaction();
		
		if ( ! assertMinRole(Role.MODERATOR)) {
			return;
		}
		
		if (you.getFaction() == myFaction) {
			sendMessage(you.getName()+" is already a member of "+myFaction.getTag());
			sendMessage("You might want to: " + new FCommandKick().getUseageTemplate(false));
			return;
		}
		
		myFaction.deinvite(you);
		
		you.sendMessage(me.getNameAndRelevant(you)+Conf.colorSystem+" revoked your invitation to "+myFaction.getTag(you));
		myFaction.sendMessage(me.getNameAndRelevant(me)+Conf.colorSystem+" revoked "+you.getNameAndRelevant(me)+"'s"+Conf.colorSystem+" invitation.");
	}
	
}
