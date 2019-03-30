package com.massivecraft.factions.cmd;

import com.massivecraft.factions.scoreboards.FScoreboard;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSB extends FCommand {

    public CmdSB() {
        this.aliases.add("sb");
        this.aliases.add("scoreboard");

        this.requirements = new CommandRequirements.Builder(Permission.SCOREBOARD)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        boolean toggleTo = !context.fPlayer.showScoreboard();
        FScoreboard board = FScoreboard.get(context.fPlayer);
        if (board == null) {
            context.player.sendMessage(TL.COMMAND_TOGGLESB_DISABLED.toString());
        } else {
            context.player.sendMessage(TL.TOGGLE_SB.toString().replace("{value}", String.valueOf(toggleTo)));
            board.setSidebarVisibility(toggleTo);
        }
        context.fPlayer.setShowScoreboard(toggleTo);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SCOREBOARD_DESCRIPTION;
    }
}
