package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqBankCommandsEnabled;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.mcore.cmd.arg.ARDouble;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.money.Money;
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
		this.addRequirements(ReqBankCommandsEnabled.get());
	}

	@Override
	public void perform()
	{
		Double amount = this.arg(0, ARDouble.get());
		if (amount == null) return;

		Faction from = this.arg(1, ARFaction.get(sender), myFaction);
		if (from == null) return;

		UPlayer to = fme;

		boolean success = Econ.transferMoney(fme, from, to, amount);

		if (success && MConf.get().logMoneyTransactions)
		{
			Factions.get().log(ChatColor.stripColor(Txt.parse("%s withdrew %s from the faction bank: %s", fme.getName(), Money.format(from, amount), from.describeTo(null))));
		}
	}
}
