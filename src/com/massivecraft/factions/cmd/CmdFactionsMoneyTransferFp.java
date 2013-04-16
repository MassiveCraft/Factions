package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFPlayer;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqBankCommandsEnabled;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.mcore.cmd.arg.ARDouble;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.util.Txt;

import org.bukkit.ChatColor;


public class CmdFactionsMoneyTransferFp extends FCommand
{
	public CmdFactionsMoneyTransferFp()
	{
		this.addAliases("fp");
		
		this.addRequiredArg("amount");
		this.addRequiredArg("faction");
		this.addRequiredArg("player");
		
		this.addRequirements(ReqHasPerm.get(Perm.MONEY_F2P.node));
		this.addRequirements(ReqBankCommandsEnabled.get());
	}
	
	@Override
	public void perform()
	{
		Double amount = this.arg(0, ARDouble.get());
		if (amount == null) return;
		
		Faction from = this.arg(1, ARFaction.get());
		if (from == null) return;
		
		FPlayer to = this.arg(2, ARFPlayer.getStartAny());
		if (to == null) return;
		
		boolean success = Econ.transferMoney(fme, from, to, amount);

		if (success && ConfServer.logMoneyTransactions)
			Factions.get().log(ChatColor.stripColor(Txt.parse("%s transferred %s from the faction \"%s\" to the player \"%s\"", fme.getName(), Econ.moneyString(amount), from.describeTo(null), to.describeTo(null))));
	}
}
