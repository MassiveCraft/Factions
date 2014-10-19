package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Permission;

public class CmdShowInvites extends FCommand {

    public CmdShowInvites() {
        super();
        aliases.add("showinvites");
        permission = Permission.SHOW_INVITES.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
    }

    @Override
    public void perform() {
        StringBuilder sb = new StringBuilder();
        for (String id : myFaction.getInvites()) {
            FPlayer fp = FPlayers.getInstance().getById(id);
            sb.append(fp != null ? fp.getName() : id).append(" ");
        }
        msg("<a>Players with pending invites: <i> %s", sb.toString().trim());
    }
}
