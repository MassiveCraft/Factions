package com.massivecraft.factions.commands;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Role;


public class FCommandCreate extends FBaseCommand {
	
	public FCommandCreate() {
		aliases.add("create");
		
		requiredParameters.add("faction tag");

		helpDescription = "Create a new faction";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermCreate(sender);
	}
	
	@Override
	public void perform() {
		
		if( isLocked() ) {
			sendLockMessage();
			return;
		}
		
		String tag = parameters.get(0);
		
		if (me.hasFaction()) {
			sendMessage("You must leave your current faction first.");
			return;
		}
		
		if (Faction.isTagTaken(tag)) {
			sendMessage("That tag is already in use.");
			return;
		}
		
		ArrayList<String> tagValidationErrors = Faction.validateTag(tag);
		if (tagValidationErrors.size() > 0) {
			sendMessage(tagValidationErrors);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostCreate)) {
			return;
		}

		Faction faction = Faction.create();
		faction.setTag(tag);
		me.setRole(Role.ADMIN);
		me.setFaction(faction);

		for (FPlayer follower : FPlayer.getAllOnline()) {
			follower.sendMessage(me.getNameAndRelevant(follower)+Conf.colorSystem+" created a new faction "+faction.getTag(follower));
		}
		
		sendMessage("You should now: " + new FCommandDescription().getUseageTemplate());
	}
	
}
