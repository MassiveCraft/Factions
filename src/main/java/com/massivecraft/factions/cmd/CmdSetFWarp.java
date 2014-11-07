package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.LazyLocation;

public class CmdSetFWarp extends FCommand {

    public CmdSetFWarp() {
        super();
        this.aliases.add("setwarp");
        this.aliases.add("sw");
        this.requiredArgs.add("warp name");
        this.senderMustBeMember = true;
        this.senderMustBeModerator = true;
        this.senderMustBePlayer = true;
        this.permission = Permission.SETWARP.node;
    }

    @Override
    public void perform() {
        if (!(fme.getRelationToLocation() == Relation.MEMBER)) {
            fme.msg("<i>You can only set warps in your faction territory.");
            return;
        }

        int maxWarps = P.p.getConfig().getInt("max-warps", 5);
        if (maxWarps <= myFaction.getWarps().size()) {
            fme.msg("<i>Your Faction already has the max amount of warps set <a>(%d).", maxWarps);
            return;
        }

        String warp = argAsString(0);
        LazyLocation loc = new LazyLocation(fme.getPlayer().getLocation());
        myFaction.setWarp(warp, loc);
        fme.msg("<i>Set warp <a>%s <i>to your location.", warp);
    }
}
