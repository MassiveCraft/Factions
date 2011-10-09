package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.Faction;

public class FCommandBalance extends FCommand
{
	public FCommandBalance()
	{
		super();
		this.aliases.add("balance");
		this.aliases.add("money");
		
		//this.requiredArgs.add("player name");
		this.optionalArgs.put("factiontag", "yours");
		
		this.permission = Permission.COMMAND_BALANCE.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		if ( ! Conf.bankEnabled)
		{
			return;
		}
		
		Faction faction = this.argAsFaction(0, fme.getFaction());
		
		// TODO MAKE HIERARCHIAL COMMAND STRUCTURE HERE
		if ( faction != fme.getFaction() && ! Permission.VIEW_ANY_FACTION_BALANCE.has(sender))
		{
			sendMessageParsed("<b>You do not have sufficient permissions to view the bank balance of other factions.");
			return;
		}
		
		if (faction == null)
		{
			sendMessageParsed("<b>Faction %s<b> could not be found.", args.get(0));
			return;
		}
	
		sendMessageParsed("<a>%s balance: %s", faction.getTag(), Econ.moneyString(faction.getMoney()));
	}
	
}
