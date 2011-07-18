package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Factions;

public class FCommandWorldNoClaim extends FBaseCommand {
	
	public FCommandWorldNoClaim() {
		aliases.add("worldnoclaim");
		
		helpDescription = "Disable claims in this world";
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
		
		if ( ! Conf.worldsNoClaiming.contains(worldName)) {
			Conf.worldsNoClaiming.add(worldName);
			me.sendMessage("Faction land claiming is now DISALLOWED in this world (\"" + worldName + "\").");
		} else {
			Conf.worldsNoClaiming.remove(worldName);
			me.sendMessage("Faction land claiming is now ALLOWED in this world (\"" + worldName + "\").");
		}
	}
	
}
