package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;


public class CmdHelp extends FCommand
{
	
	public CmdHelp()
	{
		super();
		this.aliases.add("help");
		this.aliases.add("h");
		this.aliases.add("?");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("page", "1");
		
		this.permission = Permission.HELP.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}	
	
	@Override
	public void perform()
	{
		int page = this.argAsInt(0, 1);
		
		sendMessage(p.txt.titleize("Factions Help ("+page+"/"+helpPages.size()+")"));
		
		page -= 1;
		
		if (page < 0 || page >= helpPages.size())
		{
			sendMessageParsed("<b>This page does not exist");
			return;
		}
		sendMessage(helpPages.get(page));
	}
	
	//----------------------------------------------//
	// Build the help pages
	//----------------------------------------------//
	
	public static ArrayList<ArrayList<String>> helpPages;
	
	public static void updateHelp()
	{
		helpPages = new ArrayList<ArrayList<String>>();
		ArrayList<String> pageLines;

		pageLines = new ArrayList<String>();
		pageLines.add( new CmdHelp().getUseageTemplate() );
		pageLines.add( new CmdList().getUseageTemplate() );
		pageLines.add( new CmdShow().getUseageTemplate() );
		pageLines.add( new CmdPower().getUseageTemplate() );
		pageLines.add( new CmdJoin().getUseageTemplate() );
		pageLines.add( new CmdLeave().getUseageTemplate() );
		pageLines.add( new CmdChat().getUseageTemplate() );
		pageLines.add( new CmdHome().getUseageTemplate() );
		pageLines.add( "Learn how to create a faction on the next page." );
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( new CmdCreate().getUseageTemplate() );
		pageLines.add( new CmdDescription().getUseageTemplate() );
		pageLines.add( new CmdTag().getUseageTemplate() );
		pageLines.add( "You might want to close it and use invitations:" );
		pageLines.add( new CmdOpen().getUseageTemplate() );
		pageLines.add( new CmdInvite().getUseageTemplate() );
		pageLines.add( new CmdDeinvite().getUseageTemplate() );
		pageLines.add( "And don't forget to set your home:" );
		pageLines.add( new CmdSethome().getUseageTemplate() );
		helpPages.add(pageLines);
		
		if (Econ.enabled() && Conf.bankEnabled)
		{
			pageLines = new ArrayList<String>();
			pageLines.add( "" );
			pageLines.add( "Your faction has a bank which is used to pay for certain" );
			pageLines.add( "things, so it will need to have money deposited into it." );
			pageLines.add( "" );
			pageLines.add( new CmdBalance().getUseageTemplate() );
			pageLines.add( new CmdDeposit().getUseageTemplate() );
			pageLines.add( new CmdWithdraw().getUseageTemplate() );
			pageLines.add( new CmdPay().getUseageTemplate() );
			pageLines.add( "" );
			helpPages.add(pageLines);
		}
		
		pageLines = new ArrayList<String>();
		pageLines.add( new CmdClaim().getUseageTemplate() );
		pageLines.add( new CmdAutoClaim().getUseageTemplate() );
		pageLines.add( new CmdUnclaim().getUseageTemplate() );
		pageLines.add( new CmdUnclaimall().getUseageTemplate() );
		pageLines.add( new CmdKick().getUseageTemplate() );
		pageLines.add( new CmdMod().getUseageTemplate() );
		pageLines.add( new CmdAdmin().getUseageTemplate() );
		pageLines.add( new CmdTitle().getUseageTemplate() );
		pageLines.add( "Player titles are just for fun. No rules connected to them." );
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( new CmdMap().getUseageTemplate() );
		pageLines.add( new CmdBoom().getUseageTemplate() );
		pageLines.add("");
		pageLines.add( new CmdOwner().getUseageTemplate() );
		pageLines.add( new CmdOwnerList().getUseageTemplate() );
		pageLines.add("");
		pageLines.add("Claimed land with ownership set is further protected so");
		pageLines.add("that only the owner(s), faction admin, and possibly the");
		pageLines.add("faction moderators have full access.");
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( new CmdDisband().getUseageTemplate() );
		pageLines.add("");
		pageLines.add( new CmdRelationAlly().getUseageTemplate() );
		pageLines.add( new CmdRelationNeutral().getUseageTemplate() );
		pageLines.add( new CmdRelationEnemy().getUseageTemplate() );
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
		pageLines.add( new CmdBypass().getUseageTemplate() );
		pageLines.add( new CmdSafeclaim().getUseageTemplate() );
		pageLines.add( new CmdAutoSafeclaim().getUseageTemplate() );
		pageLines.add( new CmdSafeunclaimall().getUseageTemplate() );
		pageLines.add( new CmdWarclaim().getUseageTemplate() );
		pageLines.add( new CmdAutoWarclaim().getUseageTemplate() );
		pageLines.add( new CmdWarunclaimall().getUseageTemplate() );
		pageLines.add("Note: " + new CmdUnclaim().getUseageTemplate(false) + P.p.txt.parse("<i>") + " works on safe/war zones as well.");
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add("More commands for server admins:");
		pageLines.add( new CmdPeaceful().getUseageTemplate() );
		pageLines.add( new CmdPermanent().getUseageTemplate() );
		pageLines.add("Peaceful factions are protected from PvP and land capture.");
		pageLines.add( new CmdLock().getUseageTemplate() );
		pageLines.add( new CmdReload().getUseageTemplate() );
		pageLines.add( new CmdSaveAll().getUseageTemplate() );
		pageLines.add( new CmdVersion().getUseageTemplate() );
		pageLines.add( new CmdConfig().getUseageTemplate() );
		helpPages.add(pageLines);
	}

	static
	{
		updateHelp();
	}
}

