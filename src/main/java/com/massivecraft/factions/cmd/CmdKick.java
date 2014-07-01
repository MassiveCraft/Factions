package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import org.bukkit.Bukkit;

public class CmdKick extends FCommand {

    public CmdKick() {
        super();
        this.aliases.add("kick");

        this.requiredArgs.add("player name");
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
        FPlayer you = this.argAsBestFPlayerMatch(0);
        if (you == null) { return; }

        if (fme == you) {
            msg("<b>You cannot kick yourself.");
            msg("<i>You might want to: %s", p.cmdBase.cmdLeave.getUseageTemplate(false));
            return;
        }

        Faction yourFaction = you.getFaction();

        // players with admin-level "disband" permission can bypass these requirements
        if (!Permission.KICK_ANY.has(sender)) {
            if (yourFaction != myFaction) {
                msg("%s<b> is not a member of %s", you.describeTo(fme, true), myFaction.describeTo(fme));
                return;
            }

            if (you.getRole().value >= fme.getRole().value) {
                // TODO add more informative messages.
                msg("<b>Your rank is too low to kick this player.");
                return;
            }

            if (!Conf.canLeaveWithNegativePower && you.getPower() < 0) {
                msg("<b>You cannot kick that member until their power is positive.");
                return;
            }
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
        if (!canAffordCommand(Conf.econCostKick, "to kick someone from the faction")) { return; }

        // trigger the leave event (cancellable) [reason:kicked]
        FPlayerLeaveEvent event = new FPlayerLeaveEvent(you, you.getFaction(), FPlayerLeaveEvent.PlayerLeaveReason.KICKED);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) { return; }

        // then make 'em pay (if applicable)
        if (!payForCommand(Conf.econCostKick, "to kick someone from the faction", "for kicking someone from the faction")) {
            return;
        }

        yourFaction.msg("%s<i> kicked %s<i> from the faction! :O", fme.describeTo(yourFaction, true), you.describeTo(yourFaction, true));
        you.msg("%s<i> kicked you from %s<i>! :O", fme.describeTo(you, true), yourFaction.describeTo(you));
        if (yourFaction != myFaction) {
            fme.msg("<i>You kicked %s<i> from the faction %s<i>!", you.describeTo(fme), yourFaction.describeTo(fme));
        }

        if (Conf.logFactionKick) {
            P.p.log((senderIsConsole ? "A console command" : fme.getName()) + " kicked " + you.getName() + " from the faction: " + yourFaction.getTag());
        }

        if (you.getRole() == Role.ADMIN) { yourFaction.promoteNewLeader(); }

        yourFaction.deinvite(you);
        you.resetFactionData();
    }

}
