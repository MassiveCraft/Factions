package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdBoom extends FCommand {

    public CmdBoom() {
        super();
        this.aliases.add("noboom");
        this.aliases.add("explosions");
        this.aliases.add("toggleexplosions");

        //this.requiredArgs.add("");
        this.optionalArgs.put("on/off", "flip");

        this.permission = Permission.NO_BOOM.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = true;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (!myFaction.isPeaceful()) {
            fme.msg(TL.COMMAND_BOOM_PEACEFULONLY);
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!payForCommand(Conf.econCostNoBoom, TL.COMMAND_BOOM_TOTOGGLE, TL.COMMAND_BOOM_FORTOGGLE)) {
            return;
        }

        myFaction.setPeacefulExplosionsEnabled(this.argAsBool(0, !myFaction.getPeacefulExplosionsEnabled()));

        String enabled = myFaction.noExplosionsInTerritory() ? TL.GENERIC_DISABLED.toString() : TL.GENERIC_ENABLED.toString();

        // Inform
        myFaction.msg(TL.COMMAND_BOOM_ENABLED, fme.describeTo(myFaction), enabled);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_BOOM_DESCRIPTION;
    }
}
