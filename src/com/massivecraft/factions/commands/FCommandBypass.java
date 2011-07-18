package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Factions;


public class FCommandBypass extends FBaseCommand {
	
	public FCommandBypass() {
		aliases.add("bypass");
		
		helpDescription = "Enable admin bypass mode; build/destroy anywhere";
	}

	@Override
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermAdminBypass(sender);
	}
	
	@Override
	public void perform() {
		if ( ! Conf.adminBypassPlayers.contains(player.getName())) {
			Conf.adminBypassPlayers.add(player.getName());
			me.sendMessage("You have enabled admin bypass mode. You will be able to build or destroy anywhere.");
		} else {
			Conf.adminBypassPlayers.remove(player.getName());
			me.sendMessage("You have disabled admin bypass mode.");
		}
	}
}
