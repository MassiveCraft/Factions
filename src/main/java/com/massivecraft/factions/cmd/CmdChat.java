package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class CmdChat extends FCommand {

    public CmdChat() {
        super();
        this.aliases.add("c");
        this.aliases.add("chat");

        this.optionalArgs.put("mode", "next");

        this.requirements = new CommandRequirements.Builder(Permission.CHAT)
                .memberOnly()
                .noDisableOnLock()
                .brigadier(ChatBrigadier.class)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!Conf.factionOnlyChat) {
            context.msg(TL.COMMAND_CHAT_DISABLED.toString());
            return;
        }

        String modeString = context.argAsString(0);
        ChatMode modeTarget = context.fPlayer.getChatMode().getNext();

        // If player is cycling through chat modes
        // and he is not atleast a moderator get next one
        if (modeString == null && modeTarget == ChatMode.MOD) {
            if (!context.fPlayer.getRole().isAtLeast(Role.MODERATOR)) {
                modeTarget = modeTarget.getNext();
            }
        }

        if (modeString != null) {
            modeString = modeString.toLowerCase();
            if (modeString.startsWith("m")) {
                modeTarget = ChatMode.MOD;
                if (!context.fPlayer.getRole().isAtLeast(Role.MODERATOR)) {
                    context.msg(TL.COMMAND_CHAT_INSUFFICIENTRANK);
                    return;
                }
            } else if (modeString.startsWith("p")) {
                modeTarget = ChatMode.PUBLIC;
            } else if (modeString.startsWith("a")) {
                modeTarget = ChatMode.ALLIANCE;
            } else if (modeString.startsWith("f")) {
                modeTarget = ChatMode.FACTION;
            } else if (modeString.startsWith("t")) {
                modeTarget = ChatMode.TRUCE;
            } else {
                context.msg(TL.COMMAND_CHAT_INVALIDMODE);
                return;
            }
        }

        context.fPlayer.setChatMode(modeTarget);

        if (context.fPlayer.getChatMode() == ChatMode.MOD) {
            context.msg(TL.COMMAND_CHAT_MODE_MOD);
        } else if (context.fPlayer.getChatMode() == ChatMode.PUBLIC) {
            context.msg(TL.COMMAND_CHAT_MODE_PUBLIC);
        } else if (context.fPlayer.getChatMode() == ChatMode.ALLIANCE) {
            context.msg(TL.COMMAND_CHAT_MODE_ALLIANCE);
        } else if (context.fPlayer.getChatMode() == ChatMode.TRUCE) {
            context.msg(TL.COMMAND_CHAT_MODE_TRUCE);
        } else {
            context.msg(TL.COMMAND_CHAT_MODE_FACTION);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CHAT_DESCRIPTION;
    }

    protected class ChatBrigadier implements BrigadierProvider {
        @Override
        public ArgumentBuilder<Object, ?> get(ArgumentBuilder<Object, ?> parent) {
            return parent.then(LiteralArgumentBuilder.literal("public"))
                    .then(LiteralArgumentBuilder.literal("mod"))
                    .then(LiteralArgumentBuilder.literal("alliance"))
                    .then(LiteralArgumentBuilder.literal("faction"))
                    .then(LiteralArgumentBuilder.literal("truce"));
        }
    } 

}
