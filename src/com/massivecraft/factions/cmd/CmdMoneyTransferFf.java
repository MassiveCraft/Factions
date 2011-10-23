package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;


public class CmdMoneyTransferFf extends FCommand
{
	public CmdMoneyTransferFf()
	{
		this.aliases.add("ff");
		
		this.requiredArgs.add("amount");
		this.requiredArgs.add("faction");
		this.requiredArgs.add("faction");
		
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.MONEY_F2F.node;
		this.setHelpShort("transfer f -> f");
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		double amount = this.argAsDouble(0, 0d);
		Faction from = this.argAsFaction(1);
		if (from == null) return;
		Faction to = this.argAsFaction(2);
		if (to == null) return;
		
		Econ.transferMoney(fme, from, to, amount);

		if (Conf.logMoneyTransactions)
			P.p.log(fme.getName()+" transferred "+Econ.moneyString(amount)+" from the faction \""+from.getTag()+"\" to the faction \""+to.getTag()+"\"");
	}
}
