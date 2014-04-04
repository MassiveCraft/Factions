package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;


public class CmdOwnerList extends FCommand {

    public CmdOwnerList() {
        super();
        this.aliases.add("ownerlist");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("", "");

        this.permission = Permission.OWNERLIST.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        boolean hasBypass = fme.isAdminBypassing();

        if (!hasBypass && !assertHasFaction()) {
            return;
        }

        if (!Conf.ownedAreasEnabled) {
            fme.msg("<b>Owned areas are disabled on this server.");
            return;
        }

        FLocation flocation = new FLocation(fme);

        if (Board.getFactionAt(flocation) != myFaction) {
            if (!hasBypass) {
                fme.msg("<b>This land is not claimed by your faction.");
                return;
            }

            myFaction = Board.getFactionAt(flocation);
            if (!myFaction.isNormal()) {
                fme.msg("<i>This land is not claimed by any faction, thus no owners.");
                return;
            }
        }

        String owners = myFaction.getOwnerListString(flocation);

        if (owners == null || owners.isEmpty()) {
            fme.msg("<i>No owners are set here; everyone in the faction has access.");
            return;
        }

        fme.msg("<i>Current owner(s) of this land: %s", owners);
    }
}
