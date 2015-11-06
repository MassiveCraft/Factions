package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.req.ReqBankCommandsEnabled;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;

public class CmdFactionsMoneyBalance extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsMoneyBalance()
	{
		// Aliases
		this.addAliases("balance");

		// Parameters
		this.addParameter(TypeFaction.get(), "faction", "you");

		// Requirements
		this.addRequirements(RequirementHasPerm.get(Perm.MONEY_BALANCE.node));
		this.addRequirements(ReqBankCommandsEnabled.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		Faction faction = this.readArg(msenderFaction);
					
		if (faction != msenderFaction && ! Perm.MONEY_BALANCE_ANY.has(sender, true)) return;
		
		Econ.sendBalanceInfo(msender, faction);
	}
	
}
