package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFPlayer;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqBankCommandsEnabled;
import com.massivecraft.factions.entity.FPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.mcore.cmd.arg.ARDouble;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.money.Money;
import com.massivecraft.mcore.util.Txt;

import org.bukkit.ChatColor;


public class CmdFactionsMoneyTransferPf extends FCommand
{
	public CmdFactionsMoneyTransferPf()
	{
		this.addAliases("pf");
		
		this.addRequiredArg("amount");
		this.addRequiredArg("player");
		this.addRequiredArg("faction");
		
		this.addRequirements(ReqHasPerm.get(Perm.MONEY_P2F.node));
		this.addRequirements(ReqBankCommandsEnabled.get());
	}
	
	@Override
	public void perform()
	{
		Double amount = this.arg(0, ARDouble.get());
		if (amount == null) return;
		
		FPlayer from = this.arg(1, ARFPlayer.getStartAny());
		if (from == null) return;
		
		Faction to = this.arg(2, ARFaction.get());
		if (to == null) return;
		
		boolean success = Econ.transferMoney(fme, from, to, amount);

		if (success && ConfServer.logMoneyTransactions)
		{
			Factions.get().log(ChatColor.stripColor(Txt.parse("%s transferred %s from the player \"%s\" to the faction \"%s\"", fme.getName(), Money.format(from, amount), from.describeTo(null), to.describeTo(null))));
		}
	}
}
