package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdClaimAt extends FCommand {

    public CmdClaimAt() {
        super();
        this.aliases.add("claimat");

        this.requiredArgs.add("world");
        this.requiredArgs.add("x");
        this.requiredArgs.add("z");

        this.permission = Permission.CLAIMAT.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        int x = argAsInt(1);
        int z = argAsInt(2);
        FLocation location = new FLocation(argAsString(0), x, z);
        fme.attemptClaim(myFaction, location, true);
    }

    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
