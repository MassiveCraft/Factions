package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;

public class CmdDescription extends FCommand {

    public CmdDescription() {
        super();
        this.aliases.add("desc");
        this.aliases.add("description");

        this.requiredArgs.add("desc");

        this.requirements = new CommandRequirements.Builder(Permission.DESCRIPTION)
                .memberOnly()
                .withRole(Role.MODERATOR)
                .noErrorOnManyArgs()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!context.payForCommand(Conf.econCostDesc, TL.COMMAND_DESCRIPTION_TOCHANGE, TL.COMMAND_DESCRIPTION_FORCHANGE)) {
            return;
        }

        // since "&" color tags seem to work even through plain old FPlayer.sendMessage() for some reason, we need to break those up
        // And replace all the % because it messes with string formatting and this is easy way around that.
        context.faction.setDescription(TextUtil.implode(context.args, " ").replaceAll("%", "").replaceAll("(&([a-f0-9klmnor]))", "& $2"));

        if (!Conf.broadcastDescriptionChanges) {
            context.fPlayer.msg(TL.COMMAND_DESCRIPTION_CHANGED, context.faction.describeTo(context.fPlayer));
            context.fPlayer.sendMessage(context.faction.getDescription());
            return;
        }

        // Broadcast the description to everyone
        for (FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
            fplayer.msg(TL.COMMAND_DESCRIPTION_CHANGES, context.faction.describeTo(fplayer));
            fplayer.sendMessage(context.faction.getDescription());  // players can inject "&" or "`" or "<i>" or whatever in their description; &k is particularly interesting looking
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DESCRIPTION_DESCRIPTION;
    }

}
