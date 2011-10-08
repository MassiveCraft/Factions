package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;


public class FCommandBypass extends FCommand {
	
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
		if ( ! Conf.adminBypassPlayers.contains(me.getName())) {
			Conf.adminBypassPlayers.add(me.getName());
			me.sendMessage("You have enabled admin bypass mode. You will be able to build or destroy anywhere.");
			P.log(me.getName() + " has ENABLED admin bypass mode.");
		} else {
			Conf.adminBypassPlayers.remove(me.getName());
			me.sendMessage("You have disabled admin bypass mode.");
			P.log(me.getName() + " DISABLED admin bypass mode.");
		}
	}
}
