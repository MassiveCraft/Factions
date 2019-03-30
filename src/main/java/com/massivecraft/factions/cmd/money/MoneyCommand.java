package com.massivecraft.factions.cmd.money;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCommand;

public abstract class MoneyCommand extends FCommand {

    public boolean isEnabled(CommandContext context) {
        if (!super.isEnabled(context)) {
            return false;
        }

        if (!Conf.econEnabled) {
            context.msg("<b>Faction economy features are disabled on this server.");
            return false;
        }

        if (!Conf.bankEnabled) {
            context.msg("<b>The faction bank system is disabled on this server.");
            return false;
        }

        return true;
    }

}
