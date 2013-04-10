package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.mcore.util.Txt;

public class CmdFactionsMoney extends FCommand
{
	public CmdFactionsMoneyBalance cmdMoneyBalance = new CmdFactionsMoneyBalance();
	public CmdFactionsMoneyDeposit cmdMoneyDeposit = new CmdFactionsMoneyDeposit();
	public CmdFactionsMoneyWithdraw cmdMoneyWithdraw = new CmdFactionsMoneyWithdraw();
	public CmdFactionsMoneyTransferFf cmdMoneyTransferFf = new CmdFactionsMoneyTransferFf();
	public CmdFactionsMoneyTransferFp cmdMoneyTransferFp = new CmdFactionsMoneyTransferFp();
	public CmdFactionsMoneyTransferPf cmdMoneyTransferPf = new CmdFactionsMoneyTransferPf();
	
	public CmdFactionsMoney()
	{
		super();
		this.aliases.add("money");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("","")
		
		this.isMoneyCommand = true;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
		
		this.setHelpShort("faction money commands");
		this.helpLong.add(Txt.parse("<i>The faction money commands."));
		
		this.addSubCommand(this.cmdMoneyBalance);
		this.addSubCommand(this.cmdMoneyDeposit);
		this.addSubCommand(this.cmdMoneyWithdraw);
		this.addSubCommand(this.cmdMoneyTransferFf);
		this.addSubCommand(this.cmdMoneyTransferFp);
		this.addSubCommand(this.cmdMoneyTransferPf);
	}
	
	@Override
	public void perform()
	{
		this.commandChain.add(this);
		Factions.get().cmdAutoHelp.execute(this.sender, this.args, this.commandChain);
	}
	
}
