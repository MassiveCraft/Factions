package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import net.md_5.bungee.api.chat.*;

public class CmdInvite extends FCommand {

    public CmdInvite() {
        super();
        this.aliases.add("invite");
        this.aliases.add("inv");

        this.requiredArgs.add("player name");
        //this.optionalArgs.put("", "");

        this.permission = Permission.INVITE.node;
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
            return;
        }

        if (you.getFaction() == myFaction) {
            msg(TL.COMMAND_INVITE_ALREADYMEMBER, you.getName(), myFaction.getTag());
            msg(TL.GENERIC_YOUMAYWANT.toString() + p.cmdBase.cmdKick.getUseageTemplate(false));
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!payForCommand(Conf.econCostInvite, TL.COMMAND_INVITE_TOINVITE.toString(), TL.COMMAND_INVITE_FORINVITE.toString())) {
            return;
        }

        myFaction.invite(you);
        if (!you.isOnline()) {
            return;
        }

        // Tooltips, colors, and commands only apply to the string immediately before it.
        TextComponent component = new TextComponent(fme.describeTo(you, true));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(TL.COMMAND_INVITE_CLICKTOJOIN.toString())}));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Conf.baseCommandAliases.get(0) + " join " + myFaction.getTag()));
        component.addExtra(new ComponentBuilder(TL.COMMAND_INVITE_INVITEDYOU.toString()).color(net.md_5.bungee.api.ChatColor.YELLOW).create()[0]);
        component.addExtra(new ComponentBuilder(myFaction.describeTo(you)).color(net.md_5.bungee.api.ChatColor.YELLOW).create()[0]);

        you.getPlayer().spigot().sendMessage(component);

        //you.msg("%s<i> invited you to %s", fme.describeTo(you, true), myFaction.describeTo(you));
        myFaction.msg(TL.COMMAND_INVITE_INVITED, fme.describeTo(myFaction, true), you.describeTo(myFaction));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_INVITE_DESCRIPTION;
    }

}
