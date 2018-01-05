package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Action;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPerm extends FCommand {

    public CmdPerm() {
        super();
        this.aliases.add("perm");
        this.aliases.add("perms");
        this.aliases.add("permission");
        this.aliases.add("permissions");

        this.optionalArgs.put("relation", "relation");
        this.optionalArgs.put("action", "action");
        this.optionalArgs.put("access", "access");

        this.permission = Permission.PERMISSIONS.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (optionalArgs.size() == 0) {
            // TODO: Open the GUI.
            return;
        }

        // If not opening GUI, then setting the permission manually.
        if (args.size() != 3) {
            fme.msg(TL.COMMAND_PERM_DESCRIPTION);
            return;
        }

        Relation relation = Relation.fromString(argAsString(0));
        Action action = Action.fromString(argAsString(1));
        Access access = Access.fromString(argAsString(2));
        if (relation == null) {
            fme.msg(TL.COMMAND_PERM_INVALID_RELATION);
            return;
        }
        if (action == null) {
            fme.msg(TL.COMMAND_PERM_INVALID_ACTION);
            return;
        }
        if (access == null) {
            fme.msg(TL.COMMAND_PERM_INVALID_ACCESS);
            return;
        }

        fme.getFaction().setPermission(relation, action, access);
        fme.msg(TL.COMMAND_PERM_SET, action.getName(), relation.nicename, access.name());
        P.p.log(String.format(TL.COMMAND_PERM_SET.toString(), action.getName(), relation.nicename, access.name()) + " for faction " + fme.getTag());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_PERM_DESCRIPTION;
    }

}
