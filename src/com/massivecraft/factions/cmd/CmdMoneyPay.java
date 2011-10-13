package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;


public class CmdMoneyPay extends FCommand
{
	public CmdMoneyPay()
	{
		this.aliases.add("pay");
		
		this.requiredArgs.add("amount");
		this.requiredArgs.add("faction");
		
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.MONEY_PAY.node;
		this.isMoneyCommand = true;
		this.isBankCommand = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		double amount = this.argAsDouble(0, 0);
		Faction faction = this.argAsFaction(1);
		if (faction == null) return;
		Econ.transferMoney(fme, myFaction, faction, amount);
	}
}
