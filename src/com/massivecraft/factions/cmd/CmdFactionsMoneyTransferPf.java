package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
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
		
		this.setHelpShort("transfer p -> f");
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

		if (success && ConfServer.logMoneyTransactions)
			Factions.get().log(ChatColor.stripColor(Txt.parse("%s transferred %s from the player \"%s\" to the faction \"%s\"", fme.getName(), Econ.moneyString(amount), from.describeTo(null), to.describeTo(null))));
	}
}
