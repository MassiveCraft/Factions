package com.massivecraft.factions.cmd.money;

import com.massivecraft.factions.P;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.zcore.util.TL;

public class CmdMoney extends MoneyCommand {

    public CmdMoneyBalance cmdMoneyBalance = new CmdMoneyBalance();
    public CmdMoneyDeposit cmdMoneyDeposit = new CmdMoneyDeposit();
    public CmdMoneyWithdraw cmdMoneyWithdraw = new CmdMoneyWithdraw();
    public CmdMoneyTransferFf cmdMoneyTransferFf = new CmdMoneyTransferFf();
    public CmdMoneyTransferFp cmdMoneyTransferFp = new CmdMoneyTransferFp();
    public CmdMoneyTransferPf cmdMoneyTransferPf = new CmdMoneyTransferPf();

    public CmdMoney() {
        super();
        this.aliases.add("money");

        this.helpLong.add(p.txt.parseTags(TL.COMMAND_MONEY_LONG.toString()));

        this.addSubCommand(this.cmdMoneyBalance);
        this.addSubCommand(this.cmdMoneyDeposit);
        this.addSubCommand(this.cmdMoneyWithdraw);
        this.addSubCommand(this.cmdMoneyTransferFf);
        this.addSubCommand(this.cmdMoneyTransferFp);
        this.addSubCommand(this.cmdMoneyTransferPf);
    }

    @Override
    public void perform(CommandContext context) {
        this.commandChain.add(this);
        P.p.cmdAutoHelp.execute(context, this.commandChain);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEY_DESCRIPTION;
    }

}
