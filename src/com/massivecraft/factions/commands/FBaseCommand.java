package com.massivecraft.factions.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Econ;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.TextUtil;


public class FBaseCommand {
	public List<String> aliases;
	public List<String> requiredParameters;
	public List<String> optionalParameters;
	
	public String helpNameAndParams;
	public String helpDescription;
	
	public CommandSender sender;
	public boolean senderMustBePlayer;
	public Player player;
	public FPlayer me;
	
	public List<String> parameters;
	
	private static boolean lock = false;
	
	public FBaseCommand() {
		aliases = new ArrayList<String>();
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		
		senderMustBePlayer = true;
		
		helpNameAndParams = "fail!";
		helpDescription = "no description";
	}
	
	public List<String> getAliases() {
		return aliases;
	}
		
	public void execute(CommandSender sender, List<String> parameters) {
		this.sender = sender;
		this.parameters = parameters;
		
		if ( ! validateCall()) {
			return;
		}
		
		if (sender instanceof Player) {
			this.player = (Player)sender;
			this.me = FPlayer.get(this.player);
		}
		
		perform();
	}
	
	public void perform() {
		
	}
	
	public void sendMessage(String message) {
		sender.sendMessage(Conf.colorSystem+message);
	}
	
	public void sendMessage(List<String> messages) {
		for(String message : messages) {
			this.sendMessage(message);
		}
	}
	
	public boolean validateCall() {
		if ( this.senderMustBePlayer && ! (sender instanceof Player)) {
			sendMessage("This command can only be used by ingame players.");
			return false;
		}
		
		if( ! hasPermission(sender)) {
			sendMessage("You lack the permissions to "+this.helpDescription.toLowerCase()+".");
			return false;
		}
		
		// make sure player doesn't have their access to the command revoked
		Iterator<String> iter = aliases.iterator();
		while (iter.hasNext()) {
			if (Factions.isCommandDisabled(sender, iter.next())) {
				sendMessage("You lack the permissions to "+this.helpDescription.toLowerCase()+".");
				return false;
			}
		}
		
		if (parameters.size() < requiredParameters.size()) {
			sendMessage("Usage: "+this.getUseageTemplate(false));
			return false;
		}
		
		return true;
	}
	
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermParticipate(sender);
	}
	
	// -------------------------------------------- //
	// Help and usage description
	// -------------------------------------------- //
	
	public String getUseageTemplate(boolean withDescription) {
		String ret = "";
		
		ret += Conf.colorCommand;
		
		ret += Factions.instance.getBaseCommand()+ " " +TextUtil.implode(this.getAliases(), ",")+" ";
		
		List<String> parts = new ArrayList<String>();
		
		for (String requiredParameter : this.requiredParameters) {
			parts.add("["+requiredParameter+"]");
		}
		
		for (String optionalParameter : this.optionalParameters) {
			parts.add("*["+optionalParameter+"]");
		}
		
		ret += Conf.colorParameter;
		
		ret += TextUtil.implode(parts, " ");
		
		if (withDescription) {
			ret += "  "+Conf.colorSystem + this.helpDescription;
		}
		return ret;
	}
	
	public String getUseageTemplate() {
		return getUseageTemplate(true);
	}
	
	// -------------------------------------------- //
	// Assertions
	// -------------------------------------------- //
	
	public boolean assertHasFaction() {
		if ( ! me.hasFaction()) {
			sendMessage("You are not member of any faction.");
			return false;
		}
		return true;
	}
	
	public boolean assertMinRole(Role role) {
		if (me.getRole().value < role.value) {
			sendMessage("You must be "+role+" to "+this.helpDescription+".");
			return false;
		}
		
		return true;
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
		// First we search faction names
		Faction faction = Faction.findByTag(factionName);
		if (faction != null) {
			return faction;
		}

		// Next we search player names
		FPlayer fp = FPlayer.find(factionName);
		if (fp != null) {
			return fp.getFaction();
		}
		
		if (defaultToMine && sender instanceof Player) {
			return me.getFaction();
		}
		
		sendMessage(Conf.colorSystem+"No faction or player \""+factionName+"\" was found");
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
		
		if (i.getRole().value > you.getRole().value || i.getRole().equals(Role.ADMIN) ) {
			return true;
		}
		
		if (you.getRole().equals(Role.ADMIN)) {
			i.sendMessage(Conf.colorSystem+"Only the faction admin can do that.");
		} else if (i.getRole().equals(Role.MODERATOR)) {
			i.sendMessage(Conf.colorSystem+"Moderators can't control each other...");
		} else {
			i.sendMessage(Conf.colorSystem+"You must be a faction moderator to do that.");
		}
		
		return false;
	}
	
	// if economy is enabled and they're not on the bypass list, make 'em pay; returns true unless person can't afford the cost
	public boolean payForCommand(double cost) {
		if (!Econ.enabled() || this.me == null || cost == 0.0 || Conf.adminBypassPlayers.contains(me.getName())) {
			return true;
		}

		String desc = this.helpDescription.toLowerCase();

		// pay up
		if (cost > 0.0) {
			String costString = Econ.moneyString(cost);
			if (!Econ.deductMoney(me.getName(), cost)) {
				sendMessage("It costs "+costString+" to "+desc+", which you can't currently afford.");
				return false;
			}
			sendMessage("You have paid "+costString+" to "+desc+".");
		}
		// wait... we pay you to use this command?
		else {
			String costString = Econ.moneyString(-cost);
			Econ.addMoney(me.getName(), -cost);
			sendMessage("You have been paid "+costString+" to "+desc+".");
		}
		return true;
	}
	
	public static final List<String> aliasTrue = new ArrayList<String>(Arrays.asList("true", "yes", "y", "ok", "on", "+"));
	public static final List<String> aliasFalse = new ArrayList<String>(Arrays.asList("false", "no", "n", "off", "-"));
	
	public boolean parseBool(String str) {
		return aliasTrue.contains(str.toLowerCase());
	}
	
	public void setLock(boolean newLock) {
		if( newLock ) {
			sendMessage("Factions is now locked");
		} else {
			sendMessage("Factions in now unlocked");
		}
		
		lock = newLock;
	}
	
	public boolean isLocked() {
		return lock;
	}
	
	public void sendLockMessage() {
		me.sendMessage("Factions is locked. Please try again later");
	}
}
