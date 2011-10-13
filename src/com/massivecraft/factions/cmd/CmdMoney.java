package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;

public class CmdMoney extends FCommand
{
	public CmdMoneyBalance cmdMoneyBalance = new CmdMoneyBalance();
	public CmdMoneyDeposit cmdMoneyDeposit = new CmdMoneyDeposit();
	public CmdMoneyWithdraw cmdMoneyWithdraw = new CmdMoneyWithdraw();
	public CmdMoneyPay cmdMoneyPay = new CmdMoneyPay();
	
	public CmdMoney()
	{
		super();
		this.aliases.add("money");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("","")
		
		this.isMoneyCommand = true;
		this.isBankCommand = false;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		
		this.setHelpShort("faction money commands");
		this.helpLong.add(p.txt.parseTags("<i>The faction money commands."));
		
		this.addSubCommand(this.cmdMoneyBalance);
		this.addSubCommand(this.cmdMoneyDeposit);
		this.addSubCommand(this.cmdMoneyWithdraw);
		this.addSubCommand(this.cmdMoneyPay);
	}
	
	@Override
	public void perform()
	{
		this.commandChain.add(this);
		P.p.cmdAutoHelp.execute(this.sender, this.args, this.commandChain);
	}
	
}
