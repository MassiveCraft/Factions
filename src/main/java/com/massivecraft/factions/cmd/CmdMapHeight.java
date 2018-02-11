package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdMapHeight extends FCommand {

    public CmdMapHeight() {
        super();

        this.aliases.add("mapheight");
        this.aliases.add("mh");

        this.optionalArgs.put("height", "height");

        this.permission = Permission.MAPHEIGHT.node;

        this.senderMustBePlayer = true;
    }

    @Override
    public void perform() {
        if (args.size() == 0) {
            fme.sendMessage(TL.COMMAND_MAPHEIGHT_CURRENT.format(fme.getMapHeight()));
            return;
        }

        int height = argAsInt(0);

        fme.setMapHeight(height);
        fme.sendMessage(TL.COMMAND_MAPHEIGHT_SET.format(fme.getMapHeight()));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MAPHEIGHT_DESCRIPTION;
    }

}
