package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

public class CmdDeinvite extends FCommand {

    public CmdDeinvite() {
        super();
        this.aliases.add("deinvite");
        this.aliases.add("deinv");

        this.optionalArgs.put("player name", "name");
        //this.optionalArgs.put("", "");

        this.permission = Permission.DEINVITE.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = true;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        FPlayer you = this.argAsBestFPlayerMatch(0);
        if (you == null) {
            FancyMessage msg = new FancyMessage(TL.COMMAND_DEINVITE_CANDEINVITE.toString()).color(ChatColor.GOLD);
            for (String id : myFaction.getInvites()) {
                FPlayer fp = FPlayers.getInstance().getById(id);
                String name = fp != null ? fp.getName() : id;
                msg.then(name + " ").color(ChatColor.WHITE).tooltip(TL.COMMAND_DEINVITE_CLICKTODEINVITE.format(name)).command("/" + Conf.baseCommandAliases.get(0) + " deinvite " + name);
            }
            sendFancyMessage(msg);
            return;
        }

        if (you.getFaction() == myFaction) {
            msg(TL.COMMAND_DEINVITE_ALREADYMEMBER, you.getName(), myFaction.getTag());
            msg(TL.COMMAND_DEINVITE_MIGHTWANT, p.cmdBase.cmdKick.getUseageTemplate(false));
            return;
        }

        myFaction.deinvite(you);

        you.msg(TL.COMMAND_DEINVITE_REVOKED, fme.describeTo(you), myFaction.describeTo(you));

        myFaction.msg(TL.COMMAND_DEINVITE_REVOKES, fme.describeTo(myFaction), you.describeTo(myFaction));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DEINVITE_DESCRIPTION;
    }

}
