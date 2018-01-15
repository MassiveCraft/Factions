package com.massivecraft.factions.cmd;

public class CmdDemote extends FPromoteCommand {

    public CmdDemote() {
        aliases.add("demote");
        this.relative = -1;
    }
}
