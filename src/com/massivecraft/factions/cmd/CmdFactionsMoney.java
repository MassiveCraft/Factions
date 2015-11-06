package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.req.ReqBankCommandsEnabled;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;

public class CmdFactionsMoney extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public CmdFactionsMoneyBalance cmdMoneyBalance = new CmdFactionsMoneyBalance();
	public CmdFactionsMoneyDeposit cmdMoneyDeposit = new CmdFactionsMoneyDeposit();
	public CmdFactionsMoneyWithdraw cmdMoneyWithdraw = new CmdFactionsMoneyWithdraw();
	public CmdFactionsMoneyTransferFf cmdMoneyTransferFf = new CmdFactionsMoneyTransferFf();
	public CmdFactionsMoneyTransferFp cmdMoneyTransferFp = new CmdFactionsMoneyTransferFp();
	public CmdFactionsMoneyTransferPf cmdMoneyTransferPf = new CmdFactionsMoneyTransferPf();
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsMoney()
	{
		// Children
		this.addChild(this.cmdMoneyBalance);
		this.addChild(this.cmdMoneyDeposit);
		this.addChild(this.cmdMoneyWithdraw);
		this.addChild(this.cmdMoneyTransferFf);
		this.addChild(this.cmdMoneyTransferFp);
		this.addChild(this.cmdMoneyTransferPf);
		
		// Aliases
		this.addAliases("money");

		// Requirements
		this.addRequirements(ReqBankCommandsEnabled.get());
		this.addRequirements(RequirementHasPerm.get(Perm.MONEY.node));
	}
	
}
