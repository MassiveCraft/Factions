package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.FlightDisableUtil;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.util.TL;

public class CmdFly extends FCommand {

    public CmdFly() {
        super();
        this.aliases.add("fly");

        this.optionalArgs.put("on/off/auto", "flip");

        this.permission = Permission.FLY.node;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
    }

    @Override
    public void perform() {
        if (args.size() == 0) {
            toggleFlight(!fme.isFlying(), true);
        } else if (args.size() == 1) {
            if (argAsString(0).equalsIgnoreCase("auto")) {
                // Player Wants to AutoFly
                if (Permission.FLY_AUTO.has(me, true)) {
                    fme.setAutoFlying(!fme.isAutoFlying());
                    toggleFlight(fme.isAutoFlying(), false);
                }
            } else {
                toggleFlight(argAsBool(0), true);
            }
        }
    }

    private void toggleFlight(final boolean toggle, boolean notify) {
        // If false do nothing besides set
        if (!toggle) {
            fme.setFlying(false);
            return;
        }
        // Do checks if true
        if (!fme.canFlyAtLocation()) {
            if (notify) {
                Faction factionAtLocation = Board.getInstance().getFactionAt(fme.getLastStoodAt());
                fme.msg(TL.COMMAND_FLY_NO_ACCESS, factionAtLocation.getTag(fme));
            }
            return;
        } else if (FlightDisableUtil.enemiesNearby(fme, P.p.getConfig().getInt("f-fly.enemy-radius", 7))) {
            if (notify) {
                fme.msg(TL.COMMAND_FLY_ENEMY_NEARBY);
            }
            return;
        }

        this.doWarmUp(WarmUpUtil.Warmup.FLIGHT, TL.WARMUPS_NOTIFY_FLIGHT, "Fly", new Runnable() {
            @Override
            public void run() {
                fme.setFlying(true);
            }
        }, this.p.getConfig().getLong("warmups.f-fly", 0));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FLY_DESCRIPTION;
    }

}
