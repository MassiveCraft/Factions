package com.massivecraft.factions.commands;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.util.TextUtil;


public class FCommandHelp extends FBaseCommand {
	
	public FCommandHelp() {
		aliases.add("help");
		aliases.add("h");
		aliases.add("?");
		
		optionalParameters.add("page");		
		
		helpDescription = "Display a help page";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return true;
	}
	
	@Override
	public void perform() {
		int page = 1;
		if (parameters.size() > 0) {
			try {
				page = Integer.parseInt(parameters.get(0));
			} catch (NumberFormatException e) {
				// wasn't an integer
			}
		}
		sendMessage(TextUtil.titleize("Factions Help ("+page+"/"+helpPages.size()+")"));
		page -= 1;
		if (page < 0 || page >= helpPages.size()) {
			sendMessage("This page does not exist");
			return;
		}
		sendMessage(helpPages.get(page));
	}
	
	//----------------------------------------------//
	// Build the help pages
	//----------------------------------------------//
	
	public static final ArrayList<ArrayList<String>> helpPages;
	
	static {
		helpPages = new ArrayList<ArrayList<String>>();
		ArrayList<String> pageLines;

		pageLines = new ArrayList<String>();
		pageLines.add( new FCommandHelp().getUseageTemplate() );
		pageLines.add( new FCommandList().getUseageTemplate() );
		pageLines.add( new FCommandShow().getUseageTemplate() );
		pageLines.add( new FCommandPower().getUseageTemplate() );
		pageLines.add( new FCommandJoin().getUseageTemplate() );
		pageLines.add( new FCommandLeave().getUseageTemplate() );
		pageLines.add( new FCommandChat().getUseageTemplate() );
		pageLines.add( new FCommandHome().getUseageTemplate() );
		pageLines.add( "Learn how to create a faction on the next page." );
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( new FCommandCreate().getUseageTemplate() );
		pageLines.add( new FCommandDescription().getUseageTemplate() );
		pageLines.add( new FCommandTag().getUseageTemplate() );
		pageLines.add( "You might want to close it and use invitations:" );
		pageLines.add( new FCommandOpen().getUseageTemplate() );
		pageLines.add( new FCommandInvite().getUseageTemplate() );
		pageLines.add( new FCommandDeinvite().getUseageTemplate() );
		pageLines.add( "And don't forget to set your home:" );
		pageLines.add( new FCommandSethome().getUseageTemplate() );
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( new FCommandClaim().getUseageTemplate() );
		pageLines.add( new FCommandAutoClaim().getUseageTemplate() );
		pageLines.add( new FCommandUnclaim().getUseageTemplate() );
		pageLines.add( new FCommandUnclaimall().getUseageTemplate() );
		pageLines.add( new FCommandKick().getUseageTemplate() );
		pageLines.add( new FCommandMod().getUseageTemplate() );
		pageLines.add( new FCommandAdmin().getUseageTemplate() );
		pageLines.add( new FCommandTitle().getUseageTemplate() );
		pageLines.add( "Player titles are just for fun. No rules connected to them." );
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( new FCommandMap().getUseageTemplate() );
		pageLines.add( new FCommandNoBoom().getUseageTemplate() );
		pageLines.add("");
		pageLines.add( new FCommandOwner().getUseageTemplate() );
		pageLines.add( new FCommandOwnerList().getUseageTemplate() );
		pageLines.add("");
		pageLines.add("Claimed land with ownership set is further protected so");
		pageLines.add("that only the owner(s), faction admin, and possibly the");
		pageLines.add("faction moderators have full access.");
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( new FCommandDisband().getUseageTemplate() );
		pageLines.add("");
		pageLines.add( new FCommandRelationAlly().getUseageTemplate() );
		pageLines.add( new FCommandRelationNeutral().getUseageTemplate() );
		pageLines.add( new FCommandRelationEnemy().getUseageTemplate() );
		pageLines.add("Set the relation you WISH to have with another faction.");
		pageLines.add("Your default relation with other factions will be neutral.");
		pageLines.add("If BOTH factions choose \"ally\" you will be allies.");
		pageLines.add("If ONE faction chooses \"enemy\" you will be enemies.");
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add("You can never hurt members or allies.");
		pageLines.add("You can not hurt neutrals in their own territory.");
		pageLines.add("You can always hurt enemies and players without faction.");
		pageLines.add("");
		pageLines.add("Damage from enemies is reduced in your own territory.");
		pageLines.add("When you die you lose power. It is restored over time.");
		pageLines.add("The power of a faction is the sum of all member power.");
		pageLines.add("The power of a faction determines how much land it can hold.");
		pageLines.add("You can claim land from factions with too little power.");
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add("Only faction members can build and destroy in their own");
		pageLines.add("territory. Usage of the following items is also restricted:");
		pageLines.add("Door, Chest, Furnace, Dispenser, Diode.");
		pageLines.add("");
		pageLines.add("Make sure to put pressure plates in front of doors for your");
		pageLines.add("guest visitors. Otherwise they can't get through. You can");
		pageLines.add("also use this to create member only areas.");
		pageLines.add("As dispensers are protected, you can create traps without");
		pageLines.add("worrying about those arrows getting stolen.");
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add("Finally some commands for the server admins:");
		pageLines.add( new FCommandBypass().getUseageTemplate() );
		pageLines.add( new FCommandSafeclaim().getUseageTemplate() );
		pageLines.add( new FCommandAutoSafeclaim().getUseageTemplate() );
		pageLines.add( new FCommandSafeunclaimall().getUseageTemplate() );
		pageLines.add( new FCommandWarclaim().getUseageTemplate() );
		pageLines.add( new FCommandAutoWarclaim().getUseageTemplate() );
		pageLines.add( new FCommandWarunclaimall().getUseageTemplate() );
		pageLines.add("Note: " + Conf.colorCommand + "f unclaim" + Conf.colorSystem + " works on safe/war zones as well.");
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add("More commands for server admins:");
		pageLines.add( new FCommandPeaceful().getUseageTemplate() );
		pageLines.add( new FCommandPermanent().getUseageTemplate() );
		pageLines.add("Peaceful factions are protected from PvP and land capture.");
		pageLines.add( new FCommandLock().getUseageTemplate() );
		pageLines.add( new FCommandReload().getUseageTemplate() );
		pageLines.add( new FCommandSaveAll().getUseageTemplate() );
		pageLines.add( new FCommandVersion().getUseageTemplate() );
		pageLines.add( new FCommandConfig().getUseageTemplate() );
		helpPages.add(pageLines);
	}
	
}

