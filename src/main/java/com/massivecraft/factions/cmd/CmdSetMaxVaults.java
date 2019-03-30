package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import org.bukkit.ChatColor;

public class CmdSetMaxVaults extends FCommand {

    public CmdSetMaxVaults() {
        this.aliases.add("setmaxvaults");
        this.aliases.add("smv");

        this.requiredArgs.add("faction");
        this.requiredArgs.add("number");

        this.requirements = new CommandRequirements.Builder(Permission.SETMAXVAULTS)
                .noDisableOnLock()
                .brigadier(MaxVaultBrigadier.class)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction targetFaction = context.argAsFaction(0);
        int value = context.argAsInt(1, -1);
        if (value < 0) {
            context.sender.sendMessage(ChatColor.RED + "Number must be greater than 0.");
            return;
        }

        if (targetFaction == null) {
            context.sender
                    .sendMessage(ChatColor.RED + "Couldn't find Faction: " + ChatColor.YELLOW + context.argAsString(0));
            return;
        }

        targetFaction.setMaxVaults(value);
        context.sender.sendMessage(TL.COMMAND_SETMAXVAULTS_SUCCESS.format(targetFaction.getTag(), value));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETMAXVAULTS_DESCRIPTION;
    }

    protected class MaxVaultBrigadier implements BrigadierProvider {
        @Override
        public ArgumentBuilder<Object, ?> get(ArgumentBuilder<Object, ?> parent) {
            return parent.then(RequiredArgumentBuilder.argument("faction", StringArgumentType.word())
                    .then(RequiredArgumentBuilder.argument("number", IntegerArgumentType.integer(0, 99))));
        }
    }

}
