package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSetFWarp extends FCommand {

    public CmdSetFWarp() {
        super();

        this.aliases.add("setwarp");
        this.aliases.add("sw");

        this.requiredArgs.add("warp");
        this.optionalArgs.put("password", "password");

        this.requirements = new CommandRequirements.Builder(Permission.SETWARP)
                .memberOnly()
                .withAction(PermissableAction.SETWARP)
                .withRole(Role.MODERATOR)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!(context.fPlayer.getRelationToLocation() == Relation.MEMBER)) {
            context.fPlayer.msg(TL.COMMAND_SETFWARP_NOTCLAIMED);
            return;
        }

        int maxWarps = P.p.getConfig().getInt("max-warps", 5);
        if (maxWarps <= context.faction.getWarps().size()) {
            context.fPlayer.msg(TL.COMMAND_SETFWARP_LIMIT, maxWarps);
            return;
        }

        if (!transact(context.fPlayer, context)) {
            return;
        }

        String warp = context.argAsString(0);
        String password = context.argAsString(1);

        LazyLocation loc = new LazyLocation(context.fPlayer.getPlayer().getLocation());
        context.faction.setWarp(warp, loc);
        if (password != null) {
            context.faction.setWarpPassword(warp, password);
        }
        context.fPlayer.msg(TL.COMMAND_SETFWARP_SET, warp, password != null ? password : "");
    }

    private boolean transact(FPlayer player, CommandContext context) {
        return !P.p.getConfig().getBoolean("warp-cost.enabled", false) || player.isAdminBypassing() || context.payForCommand(P.p.getConfig().getDouble("warp-cost.setwarp", 5), TL.COMMAND_SETFWARP_TOSET.toString(), TL.COMMAND_SETFWARP_FORSET.toString());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETFWARP_DESCRIPTION;
    }
}
