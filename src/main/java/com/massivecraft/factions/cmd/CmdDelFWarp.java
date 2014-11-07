package com.massivecraft.factions.cmd;

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
            myFaction.removeWarp(warp);
            fme.msg("<i>Deleted warp <a>%s", warp);
        } else {
            fme.msg("<i>Couldn't find warp <a>%s", warp);
        }
    }
}
