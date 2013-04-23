package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqBankCommandsEnabled;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.mcore.cmd.arg.ARDouble;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.money.Money;
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
		this.addRequirements(ReqBankCommandsEnabled.get());
	}

	@Override
	public void perform()
	{
		Double amount = this.arg(0, ARDouble.get());
		if (amount == null) return;

		Faction faction = this.arg(1, ARFaction.get(sender), myFaction);
		if (faction == null) return;

		boolean success = Econ.transferMoney(fme, fme, faction, amount);

		if (success && MConf.get().logMoneyTransactions)
		{
			Factions.get().log(ChatColor.stripColor(Txt.parse("%s deposited %s in the faction bank: %s", fme.getName(), Money.format(fme, amount), faction.describeTo(null))));
		}
	}

}
