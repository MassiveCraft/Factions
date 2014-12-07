package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;

import org.bukkit.Bukkit;

public class CmdUnclaim extends FCommand {

    public CmdUnclaim() {
        this.aliases.add("unclaim");
        this.aliases.add("declaim");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("", "");

        this.permission = Permission.UNCLAIM.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        FLocation flocation = new FLocation(fme);
        Faction otherFaction = Board.getInstance().getFactionAt(flocation);

        if (otherFaction.isSafeZone()) {
            if (Permission.MANAGE_SAFE_ZONE.has(sender)) {
                Board.getInstance().removeAt(flocation);
                msg(TL.COMMAND_UNCLAIM_SAFEZONE_SUCCESS);

                if (Conf.logLandUnclaims) {
                    P.p.log(TL.COMMAND_UNCLAIM_LOG.format(fme.getName(),flocation.getCoordString(),otherFaction.getTag()));
                }
            } else {
                msg(TL.COMMAND_UNCLAIM_SAFEZONE_NOPERM);
            }
            return;
        } else if (otherFaction.isWarZone()) {
            if (Permission.MANAGE_WAR_ZONE.has(sender)) {
                Board.getInstance().removeAt(flocation);
                msg(TL.COMMAND_UNCLAIM_WARZONE_SUCCESS);

                if (Conf.logLandUnclaims) {
                    P.p.log(TL.COMMAND_UNCLAIM_LOG.format(fme.getName(),flocation.getCoordString(),otherFaction.getTag()));
                }
            } else {
                msg(TL.COMMAND_UNCLAIM_WARZONE_NOPERM);
            }
            return;
        }

        if (fme.isAdminBypassing()) {
            Board.getInstance().removeAt(flocation);

            otherFaction.msg(TL.COMMAND_UNCLAIM_UNCLAIMED, fme.describeTo(otherFaction, true));
            msg(TL.COMMAND_UNCLAIM_UNCLAIMS);

            if (Conf.logLandUnclaims) {
            	P.p.log(TL.COMMAND_UNCLAIM_LOG.format(fme.getName(),flocation.getCoordString(),otherFaction.getTag()));
            }

            return;
        }

        if (!assertHasFaction()) {
            return;
        }

        if (!assertMinRole(Role.MODERATOR)) {
            return;
        }


        if (myFaction != otherFaction) {
            msg(TL.COMMAND_UNCLAIM_WRONGFACTION);
            return;
        }

        LandUnclaimEvent unclaimEvent = new LandUnclaimEvent(flocation, otherFaction, fme);
        Bukkit.getServer().getPluginManager().callEvent(unclaimEvent);
        if (unclaimEvent.isCancelled()) {
            return;
        }

        if (Econ.shouldBeUsed()) {
            double refund = Econ.calculateClaimRefund(myFaction.getLandRounded());

            if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts) {
                if (!Econ.modifyMoney(myFaction, refund, TL.COMMAND_UNCLAIM_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIM_FORUNCLAIM.toString())) {
                    return;
                }
            } else {
                if (!Econ.modifyMoney(fme, refund, TL.COMMAND_UNCLAIM_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIM_FORUNCLAIM.toString())) {
                    return;
                }
            }
        }

        Board.getInstance().removeAt(flocation);
        myFaction.msg(TL.COMMAND_UNCLAIM_FACTIONUNCLAIMED, fme.describeTo(myFaction, true));

        if (Conf.logLandUnclaims) {
        	P.p.log(TL.COMMAND_UNCLAIM_LOG.format(fme.getName(),flocation.getCoordString(),otherFaction.getTag()));
        }
    }

}
