package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;

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
        if (faction == null) {
            return;
        }

        boolean isMyFaction = fme == null ? false : faction == myFaction;

        if (isMyFaction) {
            if (!assertMinRole(Role.ADMIN)) {
                return;
            }
        } else {
            if (!Permission.DISBAND_ANY.has(sender, true)) {
                return;
            }
        }

        if (!faction.isNormal()) {
            msg(TL.COMMAND_DISBAND_IMMUTABLE.toString());
            return;
        }
        if (faction.isPermanent()) {
            msg(TL.COMMAND_DISBAND_MARKEDPERMANENT.toString());
            return;
        }

        FactionDisbandEvent disbandEvent = new FactionDisbandEvent(me, faction.getId());
        Bukkit.getServer().getPluginManager().callEvent(disbandEvent);
        if (disbandEvent.isCancelled()) {
            return;
        }

        // Send FPlayerLeaveEvent for each player in the faction
        for (FPlayer fplayer : faction.getFPlayers()) {
            Bukkit.getServer().getPluginManager().callEvent(new FPlayerLeaveEvent(fplayer, faction, FPlayerLeaveEvent.PlayerLeaveReason.DISBAND));
        }

        // Inform all players
        for (FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
            String who = senderIsConsole ? TL.GENERIC_SERVERADMIN.toString() : fme.describeTo(fplayer);
            if (fplayer.getFaction() == faction) {
                fplayer.msg(TL.COMMAND_DISBAND_BROADCAST_YOURS, who);
            } else {
                fplayer.msg(TL.COMMAND_DISBAND_BROADCAST_NOTYOURS, who, faction.getTag(fplayer));
            }
        }
        if (Conf.logFactionDisband) {
        	//TODO: Format this correctly and translate.
            P.p.log("The faction " + faction.getTag() + " (" + faction.getId() + ") was disbanded by " + (senderIsConsole ? "console command" : fme.getName()) + ".");
        }

        if (Econ.shouldBeUsed() && !senderIsConsole) {
            //Give all the faction's money to the disbander
            double amount = Econ.getBalance(faction.getAccountId());
            Econ.transferMoney(fme, faction, fme, amount, false);

            if (amount > 0.0) {
                String amountString = Econ.moneyString(amount);
                msg(TL.COMMAND_DISBAND_HOLDINGS, amountString);
                //TODO: Format this correctly and translate
                P.p.log(fme.getName() + " has been given bank holdings of " + amountString + " from disbanding " + faction.getTag() + ".");
            }
        }

        Factions.getInstance().removeFaction(faction.getId());
        FTeamWrapper.applyUpdates(faction);
    }
}
