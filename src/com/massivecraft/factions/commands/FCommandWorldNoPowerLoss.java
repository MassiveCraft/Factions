package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Factions;

public class FCommandWorldNoPowerLoss extends FBaseCommand {
	
	public FCommandWorldNoPowerLoss() {
		aliases.add("worldnopowerloss");
		
		helpDescription = "Disable power loss in this world";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermWorlds(sender);
	}
	
	@Override
	public void perform() {
		
		if( isLocked() ) {
			sendLockMessage();
			return;
		}
		
		String worldName = me.getPlayer().getWorld().getName();
		
		if ( ! Conf.worldsNoPowerLoss.contains(worldName)) {
			Conf.worldsNoPowerLoss.add(worldName);
			me.sendMessage("Power loss from death is now DISABLED in this world (\"" + worldName + "\").");
		} else {
			Conf.worldsNoPowerLoss.remove(worldName);
			me.sendMessage("Power loss from death is now ENABLED in this world (\"" + worldName + "\").");
		}
	}
	
}
