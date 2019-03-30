package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdChatSpy extends FCommand {

    public CmdChatSpy() {
        super();
        this.aliases.add("chatspy");

        this.optionalArgs.put("on/off", "flip");

        this.requirements = new CommandRequirements.Builder(Permission.CHATSPY)
                .playerOnly()
                .noDisableOnLock()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        context.fPlayer.setSpyingChat(context.argAsBool(0, !context.fPlayer.isSpyingChat()));

        if (context.fPlayer.isSpyingChat()) {
            context.fPlayer.msg(TL.COMMAND_CHATSPY_ENABLE);
            P.p.log(context.fPlayer.getName() + TL.COMMAND_CHATSPY_ENABLELOG.toString());
        } else {
            context.fPlayer.msg(TL.COMMAND_CHATSPY_DISABLE);
            P.p.log(context.fPlayer.getName() + TL.COMMAND_CHATSPY_DISABLELOG.toString());
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CHATSPY_DESCRIPTION;
    }
}