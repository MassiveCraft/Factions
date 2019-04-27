package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSethome extends FCommand {

    public CmdSethome() {
        this.aliases.add("sethome");

        this.requirements = new CommandRequirements.Builder(Permission.SETHOME)
                .memberOnly()
                .withAction(PermissableAction.SETHOME)
                .withRole(Role.MODERATOR)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!Conf.homesEnabled) {
            context.msg(TL.COMMAND_SETHOME_DISABLED);
            return;
        }

        // Can the player set the faction home HERE?
        if (!Permission.BYPASS.has(context.player) &&
                Conf.homesMustBeInClaimedTerritory &&
                Board.getInstance().getFactionAt(new FLocation(context.player)) != context.faction) {
            context.msg(TL.COMMAND_SETHOME_NOTCLAIMED);
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!context.payForCommand(Conf.econCostSethome, TL.COMMAND_SETHOME_TOSET, TL.COMMAND_SETHOME_FORSET)) {
            return;
        }

        context.faction.setHome(context.player.getLocation());

        context.faction.msg(TL.COMMAND_SETHOME_SET,context.fPlayer.describeTo(context.faction, true));
        context.faction.sendMessage(p.cmdBase.cmdHome.getUseageTemplate(context));
        /*
        if (faction != context.faction) {
            context.msg(TL.COMMAND_SETHOME_SETOTHER, faction.getTag(context.fPlayer));
        }*/
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETHOME_DESCRIPTION;
    }

}
