package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSafeunclaimall extends FCommand {

    public CmdSafeunclaimall() {
        this.aliases.add("safeunclaimall");
        this.aliases.add("safedeclaimall");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("radius", "0");

        this.permission = Permission.MANAGE_SAFE_ZONE.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;

    }

    @Override
    public void perform() {
        Board.getInstance().unclaimAll(Factions.getInstance().getSafeZone().getId());
        msg(TL.COMMAND_SAFEUNCLAIMALL_UNCLAIMED);

        if (Conf.logLandUnclaims) {
            P.p.log(TL.COMMAND_SAFEUNCLAIMALL_UNCLAIMEDLOG.format(sender.getName()));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SAFEUNCLAIMALL_DESCRIPTION;
    }

}
