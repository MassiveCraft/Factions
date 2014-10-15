package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

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
            msg("%s<i> is already a member of %s", you.getName(), myFaction.getTag());
            msg("<i>You might want to: " + p.cmdBase.cmdKick.getUseageTemplate(false));
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!payForCommand(Conf.econCostInvite, "to invite someone", "for inviting someone")) {
            return;
        }

        myFaction.invite(you);

        // Tooltips, colors, and commands only apply to the string immediately before it.
        FancyMessage message = new FancyMessage(fme.describeTo(you, true))
                                       .tooltip("Click to join!").command("f join " + myFaction.getTag())
                                       .then(" has invited you to join ").color(ChatColor.YELLOW)
                                       .tooltip("Click to join!").command("f join " + myFaction.getTag())
                                       .then(myFaction.describeTo(you))
                                       .tooltip("Click to join!").command("f join " + myFaction.getTag());

        message.send(you.getPlayer());

        //you.msg("%s<i> invited you to %s", fme.describeTo(you, true), myFaction.describeTo(you));
        myFaction.msg("%s<i> invited %s<i> to your faction.", fme.describeTo(myFaction, true), you.describeTo(myFaction));
    }

}
