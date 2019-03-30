package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;

public class CmdBoom extends FCommand {

    public CmdBoom() {
        super();
        this.aliases.add("noboom");
        this.aliases.add("explosions");
        this.aliases.add("toggleexplosions");

        this.optionalArgs.put("on/off", "flip");

        this.requirements = new CommandRequirements.Builder(Permission.NO_BOOM)
                .memberOnly()
                .withRole(Role.MODERATOR)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!context.faction.isPeaceful()) {
            context.msg(TL.COMMAND_BOOM_PEACEFULONLY);
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!context.payForCommand(Conf.econCostNoBoom, TL.COMMAND_BOOM_TOTOGGLE, TL.COMMAND_BOOM_FORTOGGLE)) {
            return;
        }

        context.faction.setPeacefulExplosionsEnabled(context.argAsBool(0, !context.faction.getPeacefulExplosionsEnabled()));

        String enabled = context.faction.noExplosionsInTerritory() ? TL.GENERIC_DISABLED.toString() : TL.GENERIC_ENABLED.toString();

        // Inform
        context.faction.msg(TL.COMMAND_BOOM_ENABLED, context.fPlayer.describeTo(context.faction), enabled);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_BOOM_DESCRIPTION;
    }
}
