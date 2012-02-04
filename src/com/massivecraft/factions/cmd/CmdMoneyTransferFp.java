package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;

import org.bukkit.ChatColor;


public class CmdMoneyTransferFp extends FCommand
{
	public CmdMoneyTransferFp()
	{
		this.aliases.add("fp");
		
		this.requiredArgs.add("amount");
		this.requiredArgs.add("faction");
		this.requiredArgs.add("player");
		
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.MONEY_F2P.node;
		this.setHelpShort("transfer f -> p");
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		double amount = this.argAsDouble(0, 0d);
		EconomyParticipator from = this.argAsFaction(1);
		if (from == null) return;
		EconomyParticipator to = this.argAsBestFPlayerMatch(2);
		if (to == null) return;
		
		boolean success = Econ.transferMoney(fme, from, to, amount);

		if (success && Conf.logMoneyTransactions)
			P.p.log(ChatColor.stripColor(P.p.txt.parse("%s transferred %s from the faction \"%s\" to the player \"%s\"", fme.getName(), Econ.moneyString(amount), from.describeTo(null), to.describeTo(null))));
	}
}
