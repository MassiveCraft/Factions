package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdMapHeight extends FCommand {

    public CmdMapHeight() {
        super();

        this.aliases.add("mapheight");
        this.aliases.add("mh");

        this.requiredArgs.add("height");

        this.permission = Permission.MAPHEIGHT.node;

        this.senderMustBePlayer = true;
    }

    @Override
    public void perform() {
        int height = argAsInt(0, -1);

        if (height == -1) {
            fme.sendMessage(TL.COMMAND_MAPHEIGHT_DESCRIPTION.toString());
            return;
        }

        fme.setMapHeight(height);
        fme.sendMessage(TL.COMMAND_MAPHEIGHT_SET.format(height));
    }

        @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MAPHEIGHT_DESCRIPTION;
    }

}
