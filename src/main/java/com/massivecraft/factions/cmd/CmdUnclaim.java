package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.SpiralTask;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

public class CmdUnclaim extends FCommand {

    public CmdUnclaim() {
        this.aliases.add("unclaim");
        this.aliases.add("declaim");

        this.optionalArgs.put("radius", "1");
        this.optionalArgs.put("faction", "your");

        this.permission = Permission.UNCLAIM.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        // Read and validate input
        int radius = this.argAsInt(0, 1); // Default to 1
        final Faction forFaction = this.argAsFaction(1, myFaction); // Default to own

        if (radius < 1) {
            msg(TL.COMMAND_CLAIM_INVALIDRADIUS);
            return;
        }

        if (radius < 2) {
            // single chunk
            unClaim(new FLocation(me));
        } else {
            // radius claim
            if (!Permission.CLAIM_RADIUS.has(sender, false)) {
                msg(TL.COMMAND_CLAIM_DENIED);
                return;
            }

            new SpiralTask(new FLocation(me), radius) {
                private int failCount = 0;
                private final int limit = Conf.radiusClaimFailureLimit - 1;

                @Override
                public boolean work() {
                    boolean success = unClaim(this.currentFLocation());
                    if (success) {
                        failCount = 0;
                    } else if (failCount++ >= limit) {
                        this.stop();
                        return false;
                    }

                    return true;
                }
            };
        }
    }

    private boolean unClaim(FLocation target) {
        Faction targetFaction = Board.getInstance().getFactionAt(target);
        if (targetFaction.isSafeZone()) {
            if (Permission.MANAGE_SAFE_ZONE.has(sender)) {
                Board.getInstance().removeAt(target);
                msg(TL.COMMAND_UNCLAIM_SAFEZONE_SUCCESS);

                if (Conf.logLandUnclaims) {
                    P.p.log(TL.COMMAND_UNCLAIM_LOG.format(fme.getName(), target.getCoordString(), targetFaction.getTag()));
                }
                return true;
            } else {
                msg(TL.COMMAND_UNCLAIM_SAFEZONE_NOPERM);
                return false;
            }
        } else if (targetFaction.isWarZone()) {
            if (Permission.MANAGE_WAR_ZONE.has(sender)) {
                Board.getInstance().removeAt(target);
                msg(TL.COMMAND_UNCLAIM_WARZONE_SUCCESS);

                if (Conf.logLandUnclaims) {
                    P.p.log(TL.COMMAND_UNCLAIM_LOG.format(fme.getName(), target.getCoordString(), targetFaction.getTag()));
                }
                return true;
            } else {
                msg(TL.COMMAND_UNCLAIM_WARZONE_NOPERM);
                return false;
            }
        }

        if (fme.isAdminBypassing()) {
            Board.getInstance().removeAt(target);

            targetFaction.msg(TL.COMMAND_UNCLAIM_UNCLAIMED, fme.describeTo(targetFaction, true));
            msg(TL.COMMAND_UNCLAIM_UNCLAIMS);

            if (Conf.logLandUnclaims) {
                P.p.log(TL.COMMAND_UNCLAIM_LOG.format(fme.getName(), target.getCoordString(), targetFaction.getTag()));
            }

            return true;
        }

        if (!assertHasFaction()) {
            return false;
        }

        if (!assertMinRole(Role.MODERATOR)) {
            return false;
        }


        if (myFaction != targetFaction) {
            msg(TL.COMMAND_UNCLAIM_WRONGFACTION);
            return false;
        }

        LandUnclaimEvent unclaimEvent = new LandUnclaimEvent(target, targetFaction, fme);
        Bukkit.getServer().getPluginManager().callEvent(unclaimEvent);
        if (unclaimEvent.isCancelled()) {
            return false;
        }

        if (Econ.shouldBeUsed()) {
            double refund = Econ.calculateClaimRefund(myFaction.getLandRounded());

            if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts) {
                if (!Econ.modifyMoney(myFaction, refund, TL.COMMAND_UNCLAIM_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIM_FORUNCLAIM.toString())) {
                    return false;
                }
            } else {
                if (!Econ.modifyMoney(fme, refund, TL.COMMAND_UNCLAIM_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIM_FORUNCLAIM.toString())) {
                    return false;
                }
            }
        }

        Board.getInstance().removeAt(target);
        myFaction.msg(TL.COMMAND_UNCLAIM_FACTIONUNCLAIMED, fme.describeTo(myFaction, true));

        if (Conf.logLandUnclaims) {
            P.p.log(TL.COMMAND_UNCLAIM_LOG.format(fme.getName(), target.getCoordString(), targetFaction.getTag()));
        }

        return true;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_UNCLAIM_DESCRIPTION;
    }

}
