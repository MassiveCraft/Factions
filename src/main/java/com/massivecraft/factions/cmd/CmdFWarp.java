package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.util.WarpGUI;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdFWarp extends FCommand {

    public CmdFWarp() {
        super();
        this.aliases.add("warp");
        this.aliases.add("warps");
        this.optionalArgs.put("warp", "warp");
        this.optionalArgs.put("password", "password");

        this.requirements = new CommandRequirements.Builder(Permission.WARP)
                .memberOnly()
                .withAction(PermissableAction.WARP)
                .withRole(Role.NORMAL)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        //TODO: check if in combat.
        if (context.args.size() == 0) {
            WarpGUI warpGUI = new WarpGUI(context.fPlayer);
            warpGUI.build();

            context.player.openInventory(warpGUI.getInventory());
        } else if (context.args.size() > 2) {
            context.fPlayer.msg(TL.COMMAND_FWARP_COMMANDFORMAT);
        } else {
            final String warpName = context.argAsString(0);
            final String passwordAttempt = context.argAsString(1);

            if (context.faction.isWarp(context.argAsString(0))) {
                // Check if requires password and if so, check if valid. CASE SENSITIVE
                if (context.faction.hasWarpPassword(warpName) && !context.faction.isWarpPassword(warpName, passwordAttempt)) {
                    context.fPlayer.msg(TL.COMMAND_FWARP_INVALID_PASSWORD);
                    return;
                }

                // Check transaction AFTER password check.
                if (!transact(context.fPlayer, context)) {
                    return;
                }
                final FPlayer fPlayer = context.fPlayer;
                final UUID uuid = context.fPlayer.getPlayer().getUniqueId();
                context.doWarmUp(WarmUpUtil.Warmup.WARP, TL.WARMUPS_NOTIFY_TELEPORT, warpName, new Runnable() {
                    @Override
                    public void run() {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            player.teleport(fPlayer.getFaction().getWarp(warpName).getLocation());
                            fPlayer.msg(TL.COMMAND_FWARP_WARPED, warpName);
                        }
                    }
                }, this.p.getConfig().getLong("warmups.f-warp", 0));
            } else {
                context.fPlayer.msg(TL.COMMAND_FWARP_INVALID_WARP, warpName);
            }
        }
    }

    private boolean transact(FPlayer player, CommandContext context) {
        return !P.p.getConfig().getBoolean("warp-cost.enabled", false) || player.isAdminBypassing() || context.payForCommand(P.p.getConfig().getDouble("warp-cost.warp", 5), TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString());
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FWARP_DESCRIPTION;
    }
}
