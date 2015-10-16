package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdMoneyBalance extends FCommand {

    public CmdMoneyBalance() {
        super();
        this.aliases.add("b");
        this.aliases.add("balance");

        //this.requiredArgs.add("");
        this.optionalArgs.put("faction", "yours");

        this.permission = Permission.MONEY_BALANCE.node;
        this.setHelpShort(TL.COMMAND_MONEYBALANCE_SHORT.toString());

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction faction = myFaction;
        if (this.argIsSet(0)) {
            faction = this.argAsFaction(0);
        }

        if (faction == null) {
            return;
        }
        if (faction != myFaction && !Permission.MONEY_BALANCE_ANY.has(sender, true)) {
            return;
        }

        Econ.sendBalanceInfo(fme, faction);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYBALANCE_DESCRIPTION;
    }

}
