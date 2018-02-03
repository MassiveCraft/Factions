package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

        Set<Relation> relations = new HashSet<>();
        Set<PermissableAction> permissableActions = new HashSet<>();

        boolean allRelations = argAsString(0).equalsIgnoreCase("all");
        boolean allActions = argAsString(1).equalsIgnoreCase("all");

        if (allRelations) {
            relations.addAll(Arrays.asList(Relation.values()));
        } else {
            Relation relation = Relation.fromString(argAsString(0));
            if (relation == null) {
                fme.msg(TL.COMMAND_PERM_INVALID_RELATION);
                return;
            }

            relations.add(relation);
        }

        if (allActions) {
            permissableActions.addAll(Arrays.asList(PermissableAction.values()));
        } else {
            PermissableAction permissableAction = PermissableAction.fromString(argAsString(1));
            if (permissableAction == null) {
                fme.msg(TL.COMMAND_PERM_INVALID_ACTION);
                return;
            }

            permissableActions.add(permissableAction);
        }

        Access access = Access.fromString(argAsString(2));

        if (access == null) {
            fme.msg(TL.COMMAND_PERM_INVALID_ACCESS);
            return;
        }

        for (Relation relation : relations) {
            for (PermissableAction permissableAction : permissableActions) {
                fme.getFaction().setPermission(relation, permissableAction, access);
            }
        }

        fme.msg(TL.COMMAND_PERM_SET, argAsString(1), access.name(), argAsString(0));
        P.p.log(String.format(TL.COMMAND_PERM_SET.toString(), argAsString(1), access.name(), argAsString(0)) + " for faction " + fme.getTag());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_PERM_DESCRIPTION;
    }

}
