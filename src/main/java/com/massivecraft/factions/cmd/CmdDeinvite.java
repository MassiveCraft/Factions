package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

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
            TextComponent component = new TextComponent(TL.COMMAND_DEINVITE_CANDEINVITE.toString());
            component.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            for (String id : myFaction.getInvites()) {
                FPlayer fp = FPlayers.getInstance().getById(id);
                String name = fp != null ? fp.getName() : id;
                TextComponent then = new TextComponent(name + " ");
                then.setColor(net.md_5.bungee.api.ChatColor.WHITE);
                then.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(TL.COMMAND_DEINVITE_CLICKTODEINVITE.format(name))}));
                then.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Conf.baseCommandAliases.get(0) + " deinvite " + name));
                component.addExtra(then);
            }
            fme.getPlayer().spigot().sendMessage(component);
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
