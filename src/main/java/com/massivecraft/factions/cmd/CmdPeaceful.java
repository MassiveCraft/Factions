package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPeaceful extends FCommand {

    public CmdPeaceful() {
        super();
        this.aliases.add("peaceful");

        this.requiredArgs.add("faction");

        this.requirements = new CommandRequirements.Builder(Permission.SET_PEACEFUL).build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.argAsFaction(0);
        if (faction == null) {
            return;
        }

        String change;
        if (faction.isPeaceful()) {
            change = TL.COMMAND_PEACEFUL_REVOKE.toString();
            faction.setPeaceful(false);
        } else {
            change = TL.COMMAND_PEACEFUL_GRANT.toString();
            faction.setPeaceful(true);
        }

        // Inform all players
        for (FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
            String blame = (context.fPlayer == null ? TL.GENERIC_SERVERADMIN.toString() :context.fPlayer.describeTo(fplayer, true));
            if (fplayer.getFaction() == faction) {
                fplayer.msg(TL.COMMAND_PEACEFUL_YOURS, blame, change);
            } else {
                fplayer.msg(TL.COMMAND_PEACEFUL_OTHER, blame, change, faction.getTag(fplayer));
            }
        }

    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_PEACEFUL_DESCRIPTION;
    }

}
