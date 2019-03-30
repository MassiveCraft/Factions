package com.massivecraft.factions.cmd.money;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdMoneyBalance extends MoneyCommand {

    public CmdMoneyBalance() {
        super();
        this.aliases.add("b");
        this.aliases.add("balance");

        //this.requiredArgs.add("");
        this.optionalArgs.put("faction", "yours");

        this.requirements = new CommandRequirements.Builder(Permission.MONEY_BALANCE).build();

        this.setHelpShort(TL.COMMAND_MONEYBALANCE_SHORT.toString());
    }

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.faction;
        if (context.argIsSet(0)) {
            faction = context.argAsFaction(0);
        }

        if (faction == null) {
            return;
        }
        if (faction != context.faction && !Permission.MONEY_BALANCE_ANY.has(context.sender, true)) {
            return;
        }

        if (context.fPlayer != null) {
            Econ.sendBalanceInfo(context.fPlayer, faction);
        } else {
            Econ.sendBalanceInfo(context.sender, faction);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYBALANCE_DESCRIPTION;
    }

}
