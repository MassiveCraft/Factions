package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Permission;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

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
        FancyMessage msg = new FancyMessage("Players with pending invites: ").color(ChatColor.GOLD);
        for (String id : myFaction.getInvites()) {
            FPlayer fp = FPlayers.getInstance().getById(id);
            String name = fp != null ? fp.getName() : id;
            msg.then(name + " ").color(ChatColor.WHITE).tooltip("Click to revoke invite for " + name).command("f deinvite " + name);
        }

        sendFancyMessage(msg);


    }


}
