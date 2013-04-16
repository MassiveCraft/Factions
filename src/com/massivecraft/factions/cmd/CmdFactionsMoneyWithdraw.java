package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.util.Txt;

import org.bukkit.ChatColor;


public class CmdFactionsMoneyWithdraw extends FCommand
{
	public CmdFactionsMoneyWithdraw()
	{
		this.addAliases("w", "withdraw");
		
		this.addRequiredArg("amount");
		this.addOptionalArg("faction", "you");
		
		this.addRequirements(ReqHasPerm.get(Perm.MONEY_WITHDRAW.node));
		
		this.setHelpShort("withdraw money");
	}
	
	@Override
	public void perform()
	{
		double amount = this.argAsDouble(0, 0d);
		EconomyParticipator faction = this.argAsFaction(1, myFaction);
		if (faction == null) return;
		boolean success = Econ.transferMoney(fme, faction, fme, amount);

		if (success && ConfServer.logMoneyTransactions)
			Factions.get().log(ChatColor.stripColor(Txt.parse("%s withdrew %s from the faction bank: %s", fme.getName(), Econ.moneyString(amount), faction.describeTo(null))));
	}
}
