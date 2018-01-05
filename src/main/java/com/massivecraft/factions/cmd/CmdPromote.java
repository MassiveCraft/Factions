package com.massivecraft.factions.cmd;

public class CmdPromote extends FPromoteCommand {

    public CmdPromote() {
        aliases.add("promote");
        aliases.add("promo");
        this.relative = 1;
    }
}
