package org.mcteam.factions.commands;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.mcteam.factions.util.TextUtil;


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
	
	public static ArrayList<ArrayList<String>> helpPages;
	
	static {
		helpPages = new ArrayList<ArrayList<String>>();
		ArrayList<String> pageLines;

		pageLines = new ArrayList<String>();
		pageLines.add( new FCommandHelp().getUseageTemplate(true, true) );
		pageLines.add( new FCommandList().getUseageTemplate(true, true) );
		pageLines.add( new FCommandShow().getUseageTemplate(true, true) );
		pageLines.add( new FCommandMap().getUseageTemplate(true, true) );
		pageLines.add( new FCommandJoin().getUseageTemplate(true, true) );
		pageLines.add( new FCommandLeave().getUseageTemplate(true, true) );
		pageLines.add( new FCommandChat().getUseageTemplate(true, true) );
		pageLines.add( new FCommandHome().getUseageTemplate(true, true) );
		pageLines.add( "Learn how to create a faction on the next page." );
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( "Create a faction using these two commands:" );
		pageLines.add( new FCommandCreate().getUseageTemplate(true, true) );
		pageLines.add( new FCommandDescription().getUseageTemplate(true, true) );
		pageLines.add( "You might wan't to close it and use invitations:" );
		pageLines.add( new FCommandOpen().getUseageTemplate(true, true) );
		pageLines.add( new FCommandInvite().getUseageTemplate(true, true) );
		pageLines.add( new FCommandDeinvite().getUseageTemplate(true, true) );
		pageLines.add( "And don't forget to set your home:" );
		pageLines.add( new FCommandSethome().getUseageTemplate(true, true) );
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( "Faction can claim land that will be protected." );
		pageLines.add( new FCommandClaim().getUseageTemplate(true, true) );
		pageLines.add( new FCommandUnclaim().getUseageTemplate(true, true) );
		pageLines.add( new FCommandTag().getUseageTemplate(true, true) );
		pageLines.add( new FCommandKick().getUseageTemplate(true, true) );
		pageLines.add( new FCommandMod().getUseageTemplate(true, true) );
		pageLines.add( new FCommandAdmin().getUseageTemplate(true, true) );
		pageLines.add( new FCommandTitle().getUseageTemplate(true, true) );
		pageLines.add( "Player titles are just for fun. No rules connected to them." );
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( new FCommandRelationAlly().getUseageTemplate(true, true) );
		pageLines.add( new FCommandRelationNeutral().getUseageTemplate(true, true) );
		pageLines.add( new FCommandRelationEnemy().getUseageTemplate(true, true) );
		pageLines.add("");
		pageLines.add("Set the relation you WISH to have with another faction.");
		pageLines.add("Your default relation with other factions will be neutral.");
		pageLines.add("");
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
		pageLines.add("Door, Chest, Furnace and Dispenser.");
		pageLines.add("");
		pageLines.add("Make sure to put pressure plates in front of doors for your");
		pageLines.add("guest visitors. Otherwise they can't get through. You can");
		pageLines.add("also use this to create member only areas.");
		pageLines.add("As dispensers are protected, you can create traps without");
		pageLines.add("worrying about those arrows getting stolen.");
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add("Finally some commands for the server admins:");
		pageLines.add( new FCommandVersion().getUseageTemplate(true, true) );
		pageLines.add( new FCommandSafeclaim().getUseageTemplate(true, true) );
		pageLines.add( new FCommandBypass().getUseageTemplate(true, true) );
		helpPages.add(pageLines);
	}
	
}

