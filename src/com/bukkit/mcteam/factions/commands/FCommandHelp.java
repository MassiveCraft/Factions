package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.util.TextUtil;

public class FCommandHelp extends FBaseCommand {
	
	public FCommandHelp() {
		aliases = new ArrayList<String>();
		aliases.add("help");
		aliases.add("h");
		aliases.add("?");
		
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		optionalParameters.add("page");
		
		permissions = "";
		
		senderMustBePlayer = false;
		
		helpDescription = "Display a help page";
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
		pageLines.add( new FCommandCreate().getUseageTemplate(true, true) );
		pageLines.add( new FCommandTag().getUseageTemplate(true, true) );
		pageLines.add( new FCommandDescription().getUseageTemplate(true, true) );
		
		helpPages.add(pageLines);
		pageLines = new ArrayList<String>();
		
		pageLines.add( new FCommandOpen().getUseageTemplate(true, true) );
		pageLines.add( new FCommandTitle().getUseageTemplate(true, true) );
		pageLines.add( new FCommandInvite().getUseageTemplate(true, true) );
		pageLines.add( new FCommandDeinvite().getUseageTemplate(true, true) );
		pageLines.add( new FCommandClaim().getUseageTemplate(true, true) );
		pageLines.add( new FCommandUnclaim().getUseageTemplate(true, true) );
		pageLines.add( new FCommandKick().getUseageTemplate(true, true) );
		pageLines.add( new FCommandMod().getUseageTemplate(true, true) );
		pageLines.add( new FCommandAdmin().getUseageTemplate(true, true) );
		
		helpPages.add(pageLines);
		pageLines = new ArrayList<String>();
		
		pageLines.add( new FCommandRelationAlly().getUseageTemplate(true, true) );
		pageLines.add( new FCommandRelationNeutral().getUseageTemplate(true, true) );
		pageLines.add( new FCommandRelationEnemy().getUseageTemplate(true, true) );
		pageLines.add("");
		pageLines.add(Conf.colorSystem+"Set the relation you WISH to have with another faction.");
		pageLines.add(Conf.colorSystem+"Your default relation with other factions will be neutral.");
		pageLines.add("");
		pageLines.add(Conf.colorSystem+"If BOTH factions choose \"ally\" you will be allies.");
		pageLines.add(Conf.colorSystem+"If ONE faction chooses \"enemy\" you will be enemies.");
		
		helpPages.add(pageLines);
		pageLines = new ArrayList<String>();
		
		pageLines.add(Conf.colorSystem+"You can never hurt members or allies.");
		pageLines.add(Conf.colorSystem+"You can not hurt neutrals in their own territory.");
		pageLines.add(Conf.colorSystem+"You can always hurt enemies and players without faction.");
		pageLines.add("");
		pageLines.add(Conf.colorSystem+"Damage from enemies is reduced in your own territory.");
		pageLines.add(Conf.colorSystem+"When you die you lose power. It is restored over time.");
		pageLines.add(Conf.colorSystem+"The power of a faction is the sum of all member power.");
		pageLines.add(Conf.colorSystem+"The power of a faction determines how much land it can hold.");
		pageLines.add(Conf.colorSystem+"You can claim land from factions with too little power.");
		
		helpPages.add(pageLines);
		pageLines = new ArrayList<String>();
		
		pageLines.add(Conf.colorSystem+"Only faction members can build and destroy in their own");
		pageLines.add(Conf.colorSystem+"territory. Usage of the following items is also restricted:");
		pageLines.add(Conf.colorSystem+"Door, Chest, Furnace and Dispenser.");
		pageLines.add(" ");
		pageLines.add(Conf.colorSystem+"Make sure to put pressure plates in front of doors for your");
		pageLines.add(Conf.colorSystem+"guest visitors. Otherwise they can't get through. You can ");
		pageLines.add(Conf.colorSystem+"also use this to create member only areas.");
		pageLines.add(Conf.colorSystem+"As dispensers are protected, you can create traps without");
		pageLines.add(Conf.colorSystem+"worrying about those arrows getting stolen.");

		helpPages.add(pageLines);
		pageLines = new ArrayList<String>();
		
		pageLines.add( new FCommandVersion().getUseageTemplate(true, true) );
		
		helpPages.add(pageLines);
	}
	
}

