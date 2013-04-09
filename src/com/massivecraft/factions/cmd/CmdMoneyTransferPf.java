package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;

import org.bukkit.ChatColor;


public class CmdMoneyTransferPf extends FCommand
{
	public CmdMoneyTransferPf()
	{
		this.aliases.add("pf");
		
		this.requiredArgs.add("amount");
		this.requiredArgs.add("player");
		this.requiredArgs.add("faction");
		
		//this.optionalArgs.put("", "");
		
		this.permission = Perm.MONEY_P2F.node;
		this.setHelpShort("transfer p -> f");
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		double amount = this.argAsDouble(0, 0d);
		EconomyParticipator from = this.argAsBestFPlayerMatch(1);
		if (from == null) return;
		EconomyParticipator to = this.argAsFaction(2);
		if (to == null) return;
		
		boolean success = Econ.transferMoney(fme, from, to, amount);

		if (success && Conf.logMoneyTransactions)
			Factions.p.log(ChatColor.stripColor(Factions.p.txt.parse("%s transferred %s from the player \"%s\" to the faction \"%s\"", fme.getName(), Econ.moneyString(amount), from.describeTo(null), to.describeTo(null))));
	}
}
