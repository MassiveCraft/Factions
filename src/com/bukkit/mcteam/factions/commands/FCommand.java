package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.Factions;
import com.bukkit.mcteam.factions.struct.Role;

public class FCommand {
	public List<String> requiredParameters;
	public List<String> optionalParameters;
	
	public String permissions;
	
	public String helpNameAndParams;
	public String helpDescription;
	
	public CommandSender sender;
	public boolean senderMustBePlayer;
	public Player player;
	public FPlayer me;
	
	public List<String> parameters;
	
	
	public FCommand() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		
		permissions = "";
		
		senderMustBePlayer = false;
		
		helpNameAndParams = "fail!";
		helpDescription = "no description";
	}
	
	public ArrayList<String> getAliases() {
		String name = this.getClass().getName().toLowerCase();
		if (name.lastIndexOf('.') > 0) {
		    name = name.substring(name.lastIndexOf('.')+1);
		}
		
		name = name.substring(8);
		
		ArrayList<String> aliases = new ArrayList<String>();
		aliases.add(name);
		
		return aliases;
	}
	
	public String getBaseName() {
		// TODO fetch from the plugin.yaml or something...
		return "f";
	}
	
	public void execute(CommandSender sender, List<String> parameters) {
		this.sender = sender;
		this.parameters = parameters;
		
		if ( ! validateCall()) {
			sendMessage("try /help factions");
			return;
		}
		
		if (this.senderMustBePlayer) {
			this.player = (Player)sender;
			this.me = FPlayer.get(this.player);
		}
		
		perform();
	}
	
	public void perform() {
		
	}
	
	public void helpRegister() {
		Factions.helpPlugin.registerCommand(this.getBaseName()+ " " +this.helpNameAndParams, this.helpDescription, Factions.instance, false, permissions);
	}
	
	public void sendMessage(String message) {
		sender.sendMessage(Conf.colorSystem+message);
	}
	
	public void sendMessage(List<String> messages) {
		for(String message : messages) {
			this.sendMessage(message);
		}
	}
	
	// Test if the number of params is correct.
	public boolean validateCall() {
		if( ! testPermission(sender)) {
			sendMessage("You do not have sufficient permissions to use this command.");
			return false;
		}
		
		if ( this.senderMustBePlayer && ! (sender instanceof Player)) {
			sendMessage("This command can only be used by ingame players.");
			return false;
		}
		
		if (parameters.size() < requiredParameters.size()) {
			int missing = requiredParameters.size() - parameters.size();
			sendMessage("Missing parameters. You must enter "+missing+" more.");
			return false;
		}
		
		if (parameters.size() > requiredParameters.size() + optionalParameters.size()) {
			sendMessage("To many parameters.");
			return false;
		}
		
		return true;
	}
	
	public boolean testPermission(CommandSender sender) {
		if (sender.isOp()) {
			return true;
		}
		
		if (this.permissions.length() == 0) {
			return true;
		}
		
		if ( ! (sender instanceof Player)) {
			return false;
		}
		
		if (Factions.Permissions == null) {
			return false;
		}
		
		Player player = (Player)sender;
		return Factions.Permissions.has(player, this.permissions);		
	}
	
	// -------------------------------------------- //
	// Commonly used logic
	// -------------------------------------------- //
	
	public FPlayer findFPlayer(String playerName, boolean defaultToMe) {
		FPlayer fp = FPlayer.find(playerName);
		
		if (fp == null) {
			if (defaultToMe) {
				return me;
			}
			sendMessage("The player \""+playerName+"\" could not be found");
		}
		
		return fp;
	}
	
	public FPlayer findFPlayer(String playerName) {
		return findFPlayer(playerName, false);
	}
	
	
	public Faction findFaction(String factionName, boolean defaultToMine) {
		// First we search player names
		FPlayer fp = FPlayer.find(factionName);
		if (fp != null) {
			return fp.getFaction();
		}
		
		// Secondly we search faction names
		Faction faction = Faction.findByTag(factionName);
		if (faction != null) {
			return faction;
		}
		
		if (defaultToMine) {
			return me.getFaction();
		}
		
		me.sendMessage(Conf.colorSystem+"No faction or player \""+factionName+"\" was found");
		return null;
	}
	
	public Faction findFaction(String factionName) {
		return findFaction(factionName, false);
	}
	
	public boolean canIAdministerYou(FPlayer i, FPlayer you) {
		if ( ! i.getFaction().equals(you.getFaction())) {
			i.sendMessage(you.getNameAndRelevant(i)+Conf.colorSystem+" is not in the same faction as you.");
			return false;
		}
		
		if (i.role.value > you.role.value || i.role.equals(Role.ADMIN) ) {
			return true;
		}
		
		if (you.role.equals(Role.ADMIN)) {
			i.sendMessage(Conf.colorSystem+"Only the faction admin can do that.");
		} else if (i.role.equals(Role.MODERATOR)) {
			i.sendMessage(Conf.colorSystem+"Moderators can't control each other...");
		} else {
			i.sendMessage(Conf.colorSystem+"You must be a faction moderator to do that.");
		}
		
		return false;
	}
}
