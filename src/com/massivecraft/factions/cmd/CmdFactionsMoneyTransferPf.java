package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.req.ReqBankCommandsEnabled;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeDouble;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsMoneyTransferPf extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsMoneyTransferPf()
	{
		// Fields
		this.setSetupEnabled(false);

		// Aliases
		this.addAliases("pf");

		// Parameters
		this.addParameter(TypeDouble.get(), "amount");
		this.addParameter(TypeMPlayer.get(), "player");
		this.addParameter(TypeFaction.get(), "faction");

		// Requirements
		this.addRequirements(RequirementHasPerm.get(Perm.MONEY_P2F));
		this.addRequirements(ReqBankCommandsEnabled.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		double amount = this.readArg();
		MPlayer from = this.readArg();
		Faction to = this.readArg();
		
		boolean success = Econ.transferMoney(msender, from, to, amount);

		if (success && MConf.get().logMoneyTransactions)
		{
			Factions.get().log(ChatColor.stripColor(Txt.parse("%s transferred %s from the player \"%s\" to the faction \"%s\"", msender.getName(), Money.format(amount), from.describeTo(null), to.describeTo(null))));
		}
	}
	
}
