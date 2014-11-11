package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;

public class CmdDelFWarp extends FCommand {

    public CmdDelFWarp() {
        super();
        this.aliases.add("delwarp");
        this.aliases.add("dw");
        this.aliases.add("deletewarp");
        this.requiredArgs.add("warp name");
        this.senderMustBeMember = true;
        this.senderMustBeModerator = true;
        this.senderMustBePlayer = true;
        this.permission = Permission.SETWARP.node;
    }

    @Override
    public void perform() {
        String warp = argAsString(0);
        if (myFaction.isWarp(warp)) {
            if (!transact(fme)) {
                return;
            }
            myFaction.removeWarp(warp);
            fme.msg("<i>Deleted warp <a>%s", warp);
        } else {
            fme.msg("<i>Couldn't find warp <a>%s", warp);
        }
    }

    private boolean transact(FPlayer player) {
        return P.p.getConfig().getBoolean("warp-cost.enabled", false) && !player.isAdminBypassing() && Econ.modifyMoney(player, P.p.getConfig().getDouble("warp-cost.delwarp", 5), "to delete warp", "for deleting warp");
    }
}
