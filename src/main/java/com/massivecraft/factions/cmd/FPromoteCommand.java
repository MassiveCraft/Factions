package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Action;
import com.massivecraft.factions.zcore.util.TL;

public class FPromoteCommand extends FCommand {

    public int relative = 0;

    public FPromoteCommand() {
        super();

        this.optionalArgs.put("player name", "name");
        //this.optionalArgs.put("", "");

        this.permission = Permission.MOD.node;
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

        Access access = myFaction.getAccess(fme, Action.PROMOTE);

        // Well this is messy.
        if (access == null || access == Access.UNDEFINED) {
            if (!assertMinRole(Role.MODERATOR)) {
                msg(TL.COMMAND_NOACCESS);
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

        // Success!
        target.setRole(promotion);
        if (target.isOnline()) {
            target.msg(TL.COMMAND_PROMOTE_TARGET, promotion.nicename);
        }

        target.msg(TL.COMMAND_PROMOTE_SUCCESS, target.getName(), promotion.nicename);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_PROMOTE_DESCRIPTION;
    }

}
