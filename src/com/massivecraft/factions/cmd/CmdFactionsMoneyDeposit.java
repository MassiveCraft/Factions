package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.util.Txt;

import org.bukkit.ChatColor;


public class CmdFactionsMoneyDeposit extends FCommand
{
	
	public CmdFactionsMoneyDeposit()
	{
		this.addAliases("d", "deposit");
		
		this.addRequiredArg("amount");
		this.addOptionalArg("faction", "you");
		
		this.addRequirements(ReqHasPerm.get(Perm.MONEY_DEPOSIT.node));
		
		this.setHelpShort("deposit money");
	}
	
	@Override
	public void perform()
	{
		double amount = this.argAsDouble(0, 0d);
		EconomyParticipator faction = this.argAsFaction(1, myFaction);
		if (faction == null) return;
		boolean success = Econ.transferMoney(fme, fme, faction, amount);

		if (success && ConfServer.logMoneyTransactions)
			Factions.get().log(ChatColor.stripColor(Txt.parse("%s deposited %s in the faction bank: %s", fme.getName(), Econ.moneyString(amount), faction.describeTo(null))));
	}
	
}
