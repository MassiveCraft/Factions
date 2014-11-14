package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class CmdKick extends FCommand {

    public CmdKick() {
        super();
        this.aliases.add("kick");

        this.optionalArgs.put("player name", "player name");
        //this.optionalArgs.put("", "");

        this.permission = Permission.KICK.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = true;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        FPlayer toKick = this.argAsBestFPlayerMatch(0);
        if (toKick == null) {
            FancyMessage msg = new FancyMessage("Players you can kick: ").color(ChatColor.GOLD);
            for (FPlayer player : myFaction.getFPlayersWhereRole(Role.NORMAL)) {
                String s = player.getName();
                msg.then(s + " ").color(ChatColor.WHITE).tooltip("Click to kick " + s).command("f kick " + s);
            }
            if (fme.getRole() == Role.ADMIN) {
                for (FPlayer player : myFaction.getFPlayersWhereRole(Role.MODERATOR)) {
                    String s = player.getName();
                    msg.then(s + " ").color(ChatColor.GRAY).tooltip("Click to kick " + s).command("f kick " + s);
                }
            }

            sendFancyMessage(msg);
        }

        if (fme == toKick) {
            msg("<b>You cannot kick yourself.");
            msg("<i>You might want to: %s", p.cmdBase.cmdLeave.getUseageTemplate(false));
            return;
        }

        Faction toKickFaction = toKick.getFaction();

        // The PlayerEntityCollection only holds online players, this was a specific issue that kept happening.
        if (toKickFaction.getTag().equalsIgnoreCase(TL.WILDERNESS.toString())) {
            sender.sendMessage("Something went wrong with getting the offline player's faction.");
            return;
        }

        // players with admin-level "disband" permission can bypass these requirements
        if (!Permission.KICK_ANY.has(sender)) {
            if (toKickFaction != myFaction) {
                msg("%s<b> is not a member of %s", toKick.describeTo(fme, true), myFaction.describeTo(fme));
                return;
            }

            if (toKick.getRole().value >= fme.getRole().value) {
                msg("<b>Your rank is too low to kick this player.");
                return;
            }

            if (!Conf.canLeaveWithNegativePower && toKick.getPower() < 0) {
                msg("<b>You cannot kick that member until their power is positive.");
                return;
            }
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
        if (!canAffordCommand(Conf.econCostKick, "to kick someone from the faction")) {
            return;
        }

        // trigger the leave event (cancellable) [reason:kicked]
        FPlayerLeaveEvent event = new FPlayerLeaveEvent(toKick, toKick.getFaction(), FPlayerLeaveEvent.PlayerLeaveReason.KICKED);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        // then make 'em pay (if applicable)
        if (!payForCommand(Conf.econCostKick, "to kick someone from the faction", "for kicking someone from the faction")) {
            return;
        }

        toKickFaction.msg("%s<i> kicked %s<i> from the faction! :O", fme.describeTo(toKickFaction, true), toKick.describeTo(toKickFaction, true));
        toKick.msg("%s<i> kicked you from %s<i>! :O", fme.describeTo(toKick, true), toKickFaction.describeTo(toKick));
        if (toKickFaction != myFaction) {
            fme.msg("<i>You kicked %s<i> from the faction %s<i>!", toKick.describeTo(fme), toKickFaction.describeTo(fme));
        }

        if (Conf.logFactionKick) {
            P.p.log((senderIsConsole ? "A console command" : fme.getName()) + " kicked " + toKick.getName() + " from the faction: " + toKickFaction.getTag());
        }

        if (toKick.getRole() == Role.ADMIN) {
            toKickFaction.promoteNewLeader();
        }

        toKickFaction.deinvite(toKick);
        toKick.resetFactionData();
    }

}
