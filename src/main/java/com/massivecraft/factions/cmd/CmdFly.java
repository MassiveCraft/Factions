package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.FlightUtil;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.util.TL;

public class CmdFly extends FCommand {

    public CmdFly() {
        super();
        this.aliases.add("fly");

        this.optionalArgs.put("on/off/auto", "flip");

        this.requirements = new CommandRequirements.Builder(Permission.FLY)
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (context.args.size() == 0) {
            toggleFlight(context, !context.fPlayer.isFlying(), true);
        } else if (context.args.size() == 1) {
            if (context.argAsString(0).equalsIgnoreCase("auto")) {
                // Player Wants to AutoFly
                if (Permission.FLY_AUTO.has(context.player, true)) {
                    context.fPlayer.setAutoFlying(!context.fPlayer.isAutoFlying());
                    toggleFlight(context, context.fPlayer.isAutoFlying(), false);
                }
            } else {
                toggleFlight(context, context.argAsBool(0), true);
            }
        }
    }

    private void toggleFlight(final CommandContext context, final boolean toggle, boolean notify) {
        // If false do nothing besides set
        if (!toggle) {
            context.fPlayer.setFlying(false);
            return;
        }
        // Do checks if true
        if (!context.fPlayer.canFlyAtLocation()) {
            if (notify) {
                Faction factionAtLocation = Board.getInstance().getFactionAt(context.fPlayer.getLastStoodAt());
                context.msg(TL.COMMAND_FLY_NO_ACCESS, factionAtLocation.getTag(context.fPlayer));
            }
            return;
        } else if (FlightUtil.instance().enemiesTask.enemiesNearby(context.fPlayer, P.p.getConfig().getInt("f-fly.enemy-radius", 7))) {
            if (notify) {
                context.msg(TL.COMMAND_FLY_ENEMY_NEARBY);
            }
        }

        context.doWarmUp(WarmUpUtil.Warmup.FLIGHT, TL.WARMUPS_NOTIFY_FLIGHT, "Fly", new Runnable() {
            @Override
            public void run() {
                context.fPlayer.setFlying(true);
            }
        }, this.p.getConfig().getLong("warmups.f-fly", 0));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FLY_DESCRIPTION;
    }

}
