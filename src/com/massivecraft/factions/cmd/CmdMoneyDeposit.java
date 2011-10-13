package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;


public class CmdMoneyDeposit extends FCommand
{
	
	public CmdMoneyDeposit()
	{
		super();
		this.aliases.add("deposit");
		
		this.requiredArgs.add("amount");
		this.optionalArgs.put("faction", "yours");
		
		this.permission = Permission.MONEY_DEPOSIT.node;
		this.isMoneyCommand = true;
		this.isBankCommand = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		double amount = this.argAsDouble(0, 0);
		Faction faction = this.argAsFaction(1, myFaction);
		if (faction == null) return;
		Econ.transferMoney(fme, fme, faction, amount);
	}
	
}
