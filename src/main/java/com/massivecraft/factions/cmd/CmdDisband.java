package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import org.bukkit.Bukkit;


public class CmdDisband extends FCommand {
    public CmdDisband() {
        super();
        this.aliases.add("disband");

        //this.requiredArgs.add("");
        this.optionalArgs.put("faction tag", "yours");

        this.permission = Permission.DISBAND.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        // The faction, default to your own.. but null if console sender.
        Faction faction = this.argAsFaction(0, fme == null ? null : myFaction);
        if (faction == null) return;

        boolean isMyFaction = fme == null ? false : faction == myFaction;

        if (isMyFaction) {
            if (!assertMinRole(Role.ADMIN)) return;
        } else {
            if (!Permission.DISBAND_ANY.has(sender, true)) {
                return;
            }
        }

        if (!faction.isNormal()) {
            msg("<i>You cannot disband the Wilderness, SafeZone, or WarZone.");
            return;
        }
        if (faction.isPermanent()) {
            msg("<i>This faction is designated as permanent, so you cannot disband it.");
            return;
        }

        FactionDisbandEvent disbandEvent = new FactionDisbandEvent(me, faction.getId());
        Bukkit.getServer().getPluginManager().callEvent(disbandEvent);
        if (disbandEvent.isCancelled()) return;

        // Send FPlayerLeaveEvent for each player in the faction
        for (FPlayer fplayer : faction.getFPlayers()) {
            Bukkit.getServer().getPluginManager().callEvent(new FPlayerLeaveEvent(fplayer, faction, FPlayerLeaveEvent.PlayerLeaveReason.DISBAND));
        }

        // Inform all players
        for (FPlayer fplayer : FPlayers.i.getOnline()) {
            String who = senderIsConsole ? "A server admin" : fme.describeTo(fplayer);
            if (fplayer.getFaction() == faction) {
                fplayer.msg("<h>%s<i> disbanded your faction.", who);
            } else {
                fplayer.msg("<h>%s<i> disbanded the faction %s.", who, faction.getTag(fplayer));
            }
        }
        if (Conf.logFactionDisband)
            P.p.log("The faction " + faction.getTag() + " (" + faction.getId() + ") was disbanded by " + (senderIsConsole ? "console command" : fme.getName()) + ".");

        if (Econ.shouldBeUsed() && !senderIsConsole) {
            //Give all the faction's money to the disbander
            double amount = Econ.getBalance(faction.getAccountId());
            Econ.transferMoney(fme, faction, fme, amount, false);

            if (amount > 0.0) {
                String amountString = Econ.moneyString(amount);
                msg("<i>You have been given the disbanded faction's bank, totaling %s.", amountString);
                P.p.log(fme.getName() + " has been given bank holdings of " + amountString + " from disbanding " + faction.getTag() + ".");
            }
        }

        faction.detach();
    }
}
