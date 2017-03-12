package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.req.ReqBankCommandsEnabled;

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
		// Requirements
		this.addRequirements(ReqBankCommandsEnabled.get());
	}
	
}
