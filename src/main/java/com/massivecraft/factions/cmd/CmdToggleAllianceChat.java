package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdToggleAllianceChat extends FCommand {
    public CmdToggleAllianceChat() {
        super();
        this.aliases.add("tac");
        this.aliases.add("togglealliancechat");

        this.disableOnLock = false;

        this.permission = Permission.TOGGLE_ALLIANCE_CHAT.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TOGGLEALLIANCECHAT_DESCRIPTION;
    }

    @Override
    public void perform() {
        if (!Conf.factionOnlyChat) {
            msg(TL.COMMAND_CHAT_DISABLED.toString());
            return;
        }

        boolean ignoreAllianceChat = fme.isIgnoreAllianceChat();

        if (ignoreAllianceChat) {
            msg(TL.COMMAND_TOGGLEALLIANCECHAT_UNIGNORE);
            fme.setIgnoreAllianceChat(false);
        } else {
            msg(TL.COMMAND_TOGGLEALLIANCECHAT_IGNORE);
            fme.setIgnoreAllianceChat(true);
        }
    }
}
