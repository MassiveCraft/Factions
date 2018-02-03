package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class FPromoteCommand extends FCommand {

    public int relative = 0;

    public FPromoteCommand() {
        super();

        this.requiredArgs.add("player");

        this.permission = Permission.PROMOTE.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        FPlayer target = this.argAsBestFPlayerMatch(0);
        if (target == null) {
            msg(TL.GENERIC_NOPLAYERFOUND, this.argAsString(0));
            return;
        }

        if (!target.getFaction().equals(myFaction)) {
            msg(TL.COMMAND_PROMOTE_WRONGFACTION, target.getName());
            return;
        }

        Access access = myFaction.getAccess(fme.getRole(), PermissableAction.PROMOTE);

        // Well this is messy.
        if (access == null || access == Access.UNDEFINED) {
            if (!assertMinRole(Role.MODERATOR)) {
                return;
            }
        } else if (access == Access.DENY) {
            msg(TL.COMMAND_NOACCESS);
            return;
        }

        Role current = target.getRole();
        Role promotion = Role.getRelative(current, +relative);
        if (promotion == null) {
            fme.msg(TL.COMMAND_PROMOTE_NOTTHATPLAYER);
            return;
        }

        String action = relative > 0 ? TL.COMMAND_PROMOTE_PROMOTED.toString() : TL.COMMAND_PROMOTE_DEMOTED.toString();

        // Success!
        target.setRole(promotion);
        if (target.isOnline()) {
            target.msg(TL.COMMAND_PROMOTE_TARGET, action, promotion.nicename);
        }

        fme.msg(TL.COMMAND_PROMOTE_SUCCESS, action, target.getName(), promotion.nicename);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_PROMOTE_DESCRIPTION;
    }

}
