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
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}	
	
	@Override
	public void perform()
	{
		if (helpPages == null) updateHelp();
		
		int page = this.argAsInt(0, 1);
		
		sendMessage(p.txt.titleize("Factions Help ("+page+"/"+helpPages.size()+")"));
		
		page -= 1;
		
		if (page < 0 || page >= helpPages.size())
		{
			msg("<b>This page does not exist");
			return;
		}
		sendMessage(helpPages.get(page));
	}
	
	//----------------------------------------------//
	// Build the help pages
	//----------------------------------------------//
	
	public ArrayList<ArrayList<String>> helpPages;
	
	public void updateHelp()
	{
		helpPages = new ArrayList<ArrayList<String>>();
		ArrayList<String> pageLines;

		pageLines = new ArrayList<String>();
		//pageLines.add( p.cmdBase.cmdHelp.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdList.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdShow.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdPower.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdJoin.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdLeave.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdHome.getUseageTemplate(true) );
		pageLines.add( p.txt.parse("<i>Learn how to create a faction on the next page.") );
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( p.cmdBase.cmdCreate.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdDescription.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdTag.getUseageTemplate(true) );
		pageLines.add( p.txt.parse("<i>You might want to close it and use invitations:" ));
		pageLines.add( p.cmdBase.cmdOpen.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdInvite.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdDeinvite.getUseageTemplate(true) );
		pageLines.add( p.txt.parse("<i>And don't forget to set your home:" ));
		pageLines.add( p.cmdBase.cmdSethome.getUseageTemplate(true) );
		helpPages.add(pageLines);
		
		if (Econ.isSetup() && Conf.econEnabled && Conf.bankEnabled)
		{
			pageLines = new ArrayList<String>();
			pageLines.add( p.txt.parse("<i>Your faction has a bank which is used to pay for certain" ));
			pageLines.add( p.txt.parse("<i>things, so it will need to have money deposited into it." ));
			pageLines.add( p.txt.parse("<i>To learn more, use the money command." ));
			pageLines.add( "" );
			pageLines.add( p.cmdBase.cmdMoney.getUseageTemplate(true) );
			pageLines.add( "" );
			pageLines.add( "" );
			pageLines.add( "" );
			helpPages.add(pageLines);
		}
		
		pageLines = new ArrayList<String>();
		pageLines.add( p.cmdBase.cmdClaim.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdAutoClaim.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdUnclaim.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdUnclaimall.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdKick.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdPromote.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdDemote.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdOfficer.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdLeader.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdTitle.getUseageTemplate(true) );
		pageLines.add( p.txt.parse("<i>Player titles are just for fun. No rules connected to them." ));
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( p.cmdBase.cmdMap.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdSeeChunks.getUseageTemplate(true) );
		pageLines.add(p.txt.parse("<i>Claimed land with ownership set is further protected so"));
		pageLines.add(p.txt.parse("<i>that only the owner(s), faction admin, and possibly the"));
		pageLines.add(p.txt.parse("<i>faction moderators have full access."));
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( p.cmdBase.cmdDisband.getUseageTemplate(true) );
		pageLines.add("");
		pageLines.add( p.cmdBase.cmdRelationAlly.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdRelationTruce.getUseageTemplate(true) ); 
		pageLines.add( p.cmdBase.cmdRelationNeutral.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdRelationEnemy.getUseageTemplate(true) );
		pageLines.add(p.txt.parse("<i>Set the relation you WISH to have with another faction."));
		pageLines.add(p.txt.parse("<i>Your default relation with other factions will be neutral."));
		pageLines.add(p.txt.parse("<i>If BOTH factions choose \"ally\" you will be allies."));
		pageLines.add(p.txt.parse("<i>If ONE faction chooses \"enemy\" you will be enemies."));
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add(p.txt.parse("<i>You can never hurt members or allies."));
		pageLines.add(p.txt.parse("<i>You can not hurt neutrals in their own territory."));
		pageLines.add(p.txt.parse("<i>You can always hurt enemies and players without faction."));
		pageLines.add("");
		pageLines.add(p.txt.parse("<i>Damage from enemies is reduced in your own territory."));
		pageLines.add(p.txt.parse("<i>When you die you lose power. It is restored over time."));
		pageLines.add(p.txt.parse("<i>The power of a faction is the sum of all member power."));
		pageLines.add(p.txt.parse("<i>The power of a faction determines how much land it can hold."));
		pageLines.add(p.txt.parse("<i>You can claim land from factions with too little power."));
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add(p.txt.parse("<i>Only faction members can build and destroy in their own"));
		pageLines.add(p.txt.parse("<i>territory. Usage of the following items is also restricted:"));
		pageLines.add(p.txt.parse("<i>Door, Chest, Furnace, Dispenser, Diode."));
		pageLines.add("");
		pageLines.add(p.txt.parse("<i>Make sure to put pressure plates in front of doors for your"));
		pageLines.add(p.txt.parse("<i>guest visitors. Otherwise they can't get through. You can"));
		pageLines.add(p.txt.parse("<i>also use this to create member only areas."));
		pageLines.add(p.txt.parse("<i>As dispensers are protected, you can create traps without"));
		pageLines.add(p.txt.parse("<i>worrying about those arrows getting stolen."));
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add("Finally some commands for the server admins:");
		pageLines.add( p.cmdBase.cmdBypass.getUseageTemplate(true) );
		pageLines.add(p.txt.parse("<c>/f claim safezone <i>claim land for the Safe Zone"));
		pageLines.add(p.txt.parse("<c>/f claim warzone <i>claim land for the War Zone"));
		pageLines.add(p.txt.parse("<c>/f autoclaim [safezone|warzone] <i>take a guess"));
		pageLines.add(p.txt.parse("<i>Note: " + p.cmdBase.cmdUnclaim.getUseageTemplate(false) + P.p.txt.parse("<i>") + " works on safe/war zones as well."));
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add(p.txt.parse("<i>More commands for server admins:"));
		pageLines.add( p.cmdBase.cmdPowerBoost.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdLock.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdReload.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdSaveAll.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdVersion.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdConfig.getUseageTemplate(true) );
		helpPages.add(pageLines);
	}
}

