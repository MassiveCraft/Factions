package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.Faction;

public class CmdBalance extends FCommand
{
	public CmdBalance()
	{
		super();
		this.aliases.add("balance");
		this.aliases.add("money");
		
		//this.requiredArgs.add("player name");
		this.optionalArgs.put("factiontag", "yours");
		
		this.permission = Permission.BALANCE.node;
		this.disableOnLock = false;
		
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
		
		Faction faction = this.argAsFaction(0, myFaction);
		
		// TODO MAKE HIERARCHIAL COMMAND STRUCTURE HERE
		if ( faction != myFaction && ! Permission.BALANCE_ANY.has(sender))
		{
			sendMessageParsed("<b>You do not have sufficient permissions to view the bank balance of other factions.");
			return;
		}
		
		if (faction == null)
		{
			sendMessageParsed("<b>Faction %s<b> could not be found.", args.get(0));
			return;
		}
	
		sendMessageParsed("<a>%s balance: %s", faction.getTag(fme), Econ.moneyString(faction.getMoney()));
	}
	
}
