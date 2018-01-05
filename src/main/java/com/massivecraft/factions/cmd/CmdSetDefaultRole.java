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

        this.senderMustBeAdmin = true;
        this.senderMustBePlayer = true;

        this.permission = Permission.DEFAULTRANK.node;
    }

    @Override
    public void perform() {
        Role target = Role.fromString(argAsString(0).toUpperCase());
        if (target == null) {
            msg(TL.COMMAND_SETDEFAULTROLE_INVALIDROLE, argAsString(0));
            return;
        }

        if (target == Role.ADMIN) {
            msg(TL.COMMAND_SETDEFAULTROLE_NOTTHATROLE, argAsString(0));
            return;
        }

        myFaction.setDefaultRole(target);
        msg(TL.COMMAND_SETDEFAULTROLE_SUCCESS, target.nicename);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETDEFAULTROLE_DESCRIPTION;
    }
}
