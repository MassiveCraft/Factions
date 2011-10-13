package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;

public class CmdMoneyWithdraw extends FCommand
{
	public CmdMoneyWithdraw()
	{
		this.aliases.add("w");
		this.aliases.add("withdraw");
		
		this.requiredArgs.add("amount");
		this.optionalArgs.put("faction", "yours");
		
		this.permission = Permission.MONEY_WITHDRAW.node;
		this.setHelpShort("withdraw money");
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		double amount = this.argAsDouble(0, 0d);
		Faction faction = this.argAsFaction(1, myFaction);
		if (faction == null) return;
		Econ.transferMoney(fme, faction, fme, amount);
	}
}
