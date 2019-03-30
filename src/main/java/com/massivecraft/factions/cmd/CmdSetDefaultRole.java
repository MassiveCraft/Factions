package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSetDefaultRole extends FCommand {

    public CmdSetDefaultRole() {
        super();

        this.aliases.add("defaultrole");
        this.aliases.add("defaultrank");
        this.aliases.add("default");
        this.aliases.add("def");
        this.requiredArgs.add("role");

        this.requirements = new CommandRequirements.Builder(Permission.DEFAULTRANK)
                .memberOnly()
                .withRole(Role.ADMIN)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        Role target = Role.fromString(context.argAsString(0).toUpperCase());
        if (target == null) {
            context.msg(TL.COMMAND_SETDEFAULTROLE_INVALIDROLE, context.argAsString(0));
            return;
        }

        if (target == Role.ADMIN) {
            context.msg(TL.COMMAND_SETDEFAULTROLE_NOTTHATROLE, context.argAsString(0));
            return;
        }

        context.faction.setDefaultRole(target);
        context.msg(TL.COMMAND_SETDEFAULTROLE_SUCCESS, target.nicename);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETDEFAULTROLE_DESCRIPTION;
    }
}
