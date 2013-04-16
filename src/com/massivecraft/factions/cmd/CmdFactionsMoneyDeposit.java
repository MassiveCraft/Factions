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
		super();
		
		this.addAliases("d", "deposit");
		
		this.requiredArgs.add("amount");
		this.optionalArgs.put("faction", "your");
		
		this.addRequirements(ReqHasPerm.get(Perm.MONEY_DEPOSIT.node));
		
		this.setHelpShort("deposit money");
		
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
		boolean success = Econ.transferMoney(fme, fme, faction, amount);

		if (success && ConfServer.logMoneyTransactions)
			Factions.get().log(ChatColor.stripColor(Txt.parse("%s deposited %s in the faction bank: %s", fme.getName(), Econ.moneyString(amount), faction.describeTo(null))));
	}
	
}
