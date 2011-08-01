package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FPlayer;


public class FCommandPower extends FBaseCommand {
	
	public FCommandPower() {
		aliases.add("power");
		aliases.add("pow");
		
		senderMustBePlayer = false;
		
		optionalParameters.add("player name");
		
		helpDescription = "show player power info";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return true;
	}
	
	@Override
	public void perform() {
		FPlayer target;
		if (parameters.size() > 0) {
			if (!Factions.hasPermViewAnyPower(player)) {
				me.sendMessage("You do not have the appropriate permission to view another player's power level.");
				return;
			}
			target = findFPlayer(parameters.get(0), false);
		} else if (!(sender instanceof Player)) {
			sendMessage("From the console, you must specify a player (f power <player name>).");
			return;
		} else {
			target = me;
		}

		if (target == null) {
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostPower)) {
			return;
		}

		sendMessage(target.getNameAndRelevant(me)+Conf.colorChrome+" - Power / Maxpower: "+Conf.colorSystem+target.getPowerRounded()+" / "+target.getPowerMaxRounded());
	}
	
}
