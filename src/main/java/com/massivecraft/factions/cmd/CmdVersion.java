package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;


public class CmdVersion extends FCommand {

    public CmdVersion() {
        this.aliases.add("version");
        this.aliases.add("ver");

        this.requirements = new CommandRequirements.Builder(Permission.VERSION).noDisableOnLock().build();
    }

    @Override
    public void perform(CommandContext context) {
        context.msg(TL.COMMAND_VERSION_VERSION, P.p.getDescription().getFullName());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_VERSION_DESCRIPTION;
    }
}
