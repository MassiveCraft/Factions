package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;

import org.bukkit.ChatColor;


public class CmdMoneyWithdraw extends FCommand
{
	public CmdMoneyWithdraw()
	{
		this.aliases.add("w");
		this.aliases.add("withdraw");
		
		this.requiredArgs.add("amount");
		this.optionalArgs.put("faction", "your");
		
		this.permission = Permission.MONEY_WITHDRAW.node;
		this.setHelpShort("withdraw money");
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		double amount = this.argAsDouble(0, 0d);
		EconomyParticipator faction = this.argAsFaction(1, myFaction);
		if (faction == null) return;
		boolean success = Econ.transferMoney(fme, faction, fme, amount);

		if (success && Conf.logMoneyTransactions)
			P.p.log(ChatColor.stripColor(P.p.txt.parse("%s withdrew %s from the faction bank: %s", fme.getName(), Econ.moneyString(amount), faction.describeTo(null))));
	}
}
