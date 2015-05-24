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
        TextComponent component = new TextComponent(TL.COMMAND_SHOWINVITES_PENDING.toString());
        component.setColor(net.md_5.bungee.api.ChatColor.GOLD);
        for (String id : myFaction.getInvites()) {
            FPlayer fp = FPlayers.getInstance().getById(id);
            String name = fp != null ? fp.getName() : id;
            TextComponent then = new TextComponent(name + " ");
            then.setColor(net.md_5.bungee.api.ChatColor.WHITE);
            then.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(TL.COMMAND_SHOWINVITES_CLICKTOREVOKE.format(name))}));
            then.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Conf.baseCommandAliases.get(0) + " deinvite " + name));
            component.addExtra(then);
        }

        fme.getPlayer().spigot().sendMessage(component);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SHOWINVITES_DESCRIPTION;
    }


}
