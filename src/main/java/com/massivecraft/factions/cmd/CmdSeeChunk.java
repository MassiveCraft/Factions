package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.SeeChunkUtil;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSeeChunk extends FCommand {

    private boolean useParticles;

    public CmdSeeChunk() {
        super();
        aliases.add("seechunk");
        aliases.add("sc");

        permission = Permission.SEECHUNK.node;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;

        useParticles = P.p.getConfig().getBoolean("see-chunk.particles", true);
    }

    @Override
    public void perform() {
        if (useParticles) {
            boolean toggle = false;
            if (args.size() == 0) {
                toggle = !fme.isSeeingChunk();
            } else if (args.size() == 1) {
                toggle = argAsBool(0);
            }
            fme.setSeeingChunk(toggle);
            fme.msg(TL.COMMAND_SEECHUNK_TOGGLE, toggle ? "enabled" : "disabled");
        } else {
            SeeChunkUtil.showPillars(me, fme, null, false);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SEECHUNK_DESCRIPTION;
    }

}
