package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsMoneyBalance extends FCommand
{
	public CmdFactionsMoneyBalance()
	{
		this.addAliases("b", "balance");
		
		this.addOptionalArg("faction", "you");
		
		this.addRequirements(ReqHasPerm.get(Perm.MONEY_BALANCE.node));
		
		this.setHelpShort("show faction balance");
	}
	
	@Override
	public void perform()
	{
		Faction faction = this.arg(0, ARFaction.get(), myFaction);
		if (faction == null) return;
			
		if (faction != myFaction && ! Perm.MONEY_BALANCE_ANY.has(sender, true)) return;
		
		Econ.sendBalanceInfo(fme, faction);
	}
	
}
