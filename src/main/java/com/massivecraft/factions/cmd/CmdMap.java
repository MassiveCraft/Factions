package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;


public class CmdMap extends FCommand {

    public CmdMap() {
        super();
        this.aliases.add("map");

        //this.requiredArgs.add("");
        this.optionalArgs.put("on/off", "once");

        this.permission = Permission.MAP.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (this.argIsSet(0)) {
            if (this.argAsBool(0, !fme.isMapAutoUpdating())) {
                // Turn on

                // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
                if (!payForCommand(Conf.econCostMap, "to show the map", "for showing the map")) {
                    return;
                }

                fme.setMapAutoUpdating(true);
                msg(TL.COMMAND_MAP_UPDATE_ENABLED);

                // And show the map once
                showMap();
            } else {
                // Turn off
                fme.setMapAutoUpdating(false);
                msg(TL.COMMAND_MAP_UPDATE_DISABLED);
            }
        } else {
            // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
            if (!payForCommand(Conf.econCostMap, TL.COMMAND_MAP_TOSHOW, TL.COMMAND_MAP_FORSHOW)) {
                return;
            }

            showMap();
        }
    }

    public void showMap() {
        sendMessage(Board.getInstance().getMap(myFaction, new FLocation(fme), fme.getPlayer().getLocation().getYaw()));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MAP_DESCRIPTION;
    }

}
