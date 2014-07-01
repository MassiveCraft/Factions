package com.massivecraft.factions.zcore;

import com.massivecraft.factions.P;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FCommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (P.p.handleCommand(sender, cmd.getName() + args)) {
            if (P.p.logPlayerCommands()) {
                Bukkit.getLogger().info("[PLAYER_COMMAND] " + sender.getName() + ": " + cmd.getName() + args);
            }
        }
        return false;
    }
}
