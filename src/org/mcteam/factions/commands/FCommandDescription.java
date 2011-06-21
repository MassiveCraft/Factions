package org.mcteam.factions.commands;

import org.mcteam.factions.Conf;
import org.mcteam.factions.FPlayer;
import org.mcteam.factions.struct.Role;
import org.mcteam.factions.util.TextUtil;

public class FCommandDescription extends FBaseCommand {
	
	public FCommandDescription() {
		aliases.add("desc");
		
		requiredParameters.add("desc");
		
		helpDescription = "Change the faction description";
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
		
		me.getFaction().setDescription(TextUtil.implode(parameters));
		
		// Broadcast the description to everyone
		for (FPlayer fplayer : FPlayer.getAllOnline()) {
			fplayer.sendMessage("The faction "+fplayer.getRelationColor(me)+me.getFaction().getTag()+Conf.colorSystem+" changed their description to:");
			fplayer.sendMessage(me.getFaction().getDescription());
		}
	}
	
}
