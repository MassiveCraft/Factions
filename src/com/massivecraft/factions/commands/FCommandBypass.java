package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;


public class FCommandBypass extends FBaseCommand {
	
	public FCommandBypass() {
		aliases.add("bypass");
		
		helpDescription = "Enable admin bypass mode; build/destroy anywhere";
	}

	@Override
	public boolean hasPermission(CommandSender sender) {
		return P.hasPermAdminBypass(sender);
	}
	
	@Override
	public void perform() {
		if ( ! Conf.adminBypassPlayers.contains(player.getName())) {
			Conf.adminBypassPlayers.add(player.getName());
			me.sendMessage("You have enabled admin bypass mode. You will be able to build or destroy anywhere.");
			P.log(player.getName() + " has ENABLED admin bypass mode.");
		} else {
			Conf.adminBypassPlayers.remove(player.getName());
			me.sendMessage("You have disabled admin bypass mode.");
			P.log(player.getName() + " DISABLED admin bypass mode.");
		}
	}
}
