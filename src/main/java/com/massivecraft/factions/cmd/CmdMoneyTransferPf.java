package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

import org.bukkit.ChatColor;


public class CmdMoneyTransferPf extends FCommand {

    public CmdMoneyTransferPf() {
        this.aliases.add("pf");

        this.requiredArgs.add("amount");
        this.requiredArgs.add("player");
        this.requiredArgs.add("faction");

        //this.optionalArgs.put("", "");

        this.permission = Permission.MONEY_P2F.node;
        this.setHelpShort(TL.COMMAND_MONEYTRANSFERPF_SHORT.toString());

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        double amount = this.argAsDouble(0, 0d);
        EconomyParticipator from = this.argAsBestFPlayerMatch(1);
        if (from == null) {
            return;
        }
        EconomyParticipator to = this.argAsFaction(2);
        if (to == null) {
            return;
        }

        boolean success = Econ.transferMoney(fme, from, to, amount);

        if (success && Conf.logMoneyTransactions) {
            P.p.log(ChatColor.stripColor(P.p.txt.parse(TL.COMMAND_MONEYTRANSFERPF_TRANSFER.toString(), fme.getName(), Econ.moneyString(amount), from.describeTo(null), to.describeTo(null))));
        }
    }
}
