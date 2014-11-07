package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;

public class CmdFWarp extends FCommand {

    public CmdFWarp() {
        super();
        this.aliases.add("warp");
        this.aliases.add("warps");
        this.optionalArgs.put("warpname", "warpname");

        this.permission = Permission.WARP.node;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
    }

    @Override
    public void perform() {
        //TODO: check if in combat.
        if (args.size() == 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : myFaction.getWarps().keySet()) {
                sb.append(s + " ");
            }
            fme.msg("<i>Warps: <a>" + sb.toString().trim());
        } else if (args.size() > 1) {
            fme.msg("<i>/f warp <warpname>");
        } else {
            String warpName = argAsString(0);
            if (myFaction.isWarp(argAsString(0))) {
                fme.getPlayer().teleport(myFaction.getWarp(warpName).getLocation());
                fme.msg("<i>Warped to <a>%s", warpName);
            } else {
                fme.msg("<i>Couldn't find warp <a>%s", warpName);
            }
        }
    }
}
