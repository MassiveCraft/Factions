package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.TextUtil;

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

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostDesc)) {
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
