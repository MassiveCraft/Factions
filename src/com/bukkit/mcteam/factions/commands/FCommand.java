package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.Factions;

public class FCommand {
	public List<String> requiredParameters;
	public List<String> optionalParameters;
	
	public String permissions;
	
	public String helpNameAndParams;
	public String helpDescription;
	
	public CommandSender sender;
	public boolean senderMustBePlayer;
	public Player player;
	public FPlayer fplayer;
	
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
			this.fplayer = FPlayer.get(this.player);
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
}
