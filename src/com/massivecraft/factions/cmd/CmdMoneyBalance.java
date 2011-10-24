package com.massivecraft.factions.cmd;

import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.Faction;

public class CmdMoneyBalance extends FCommand
{
	public CmdMoneyBalance()
	{
		super();
		this.aliases.add("b");
		this.aliases.add("balance");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction", "your");
		
		this.permission = Permission.MONEY_BALANCE.node;
		this.setHelpShort("show faction balance");
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		Faction faction = myFaction;
		if (this.argIsSet(0))
		{
			faction = this.argAsFaction(0);
		}
			
		if (faction == null) return;
		if (faction != myFaction && ! Permission.MONEY_BALANCE_ANY.has(sender, true)) return;
		
		Econ.sendBalanceInfo(fme, faction);
	}
	
}
