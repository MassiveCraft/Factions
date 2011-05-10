package org.mcteam.factions.commands;

import org.mcteam.factions.Conf;
import org.mcteam.factions.FPlayer;
import org.mcteam.factions.Faction;
import org.mcteam.factions.struct.Role;

public class FCommandDeinvite extends FBaseCommand {
	
	public FCommandDeinvite() {
		aliases.add("deinvite");
		aliases.add("deinv");
		
		requiredParameters.add("player name");
		
		helpDescription = "Remove a pending invitation";
	}
	
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
