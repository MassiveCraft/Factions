package com.massivecraft.factions.cmd;

import org.bukkit.command.ConsoleCommandSender;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Conf.Backend;
import com.massivecraft.factions.zcore.persist.json.FactionsJSON;

public class CmdConvert extends FCommand {

    public CmdConvert() {
        this.aliases.add("convert");

        this.requiredArgs.add("[MYSQL|JSON]");
    }

    @Override
    public void perform() {
        if (!(this.sender instanceof ConsoleCommandSender)) {
            this.sender.sendMessage("Console only command");
        }
        Backend nb = Backend.valueOf(this.argAsString(0).toUpperCase());
        if (nb == Conf.backEnd) {
            this.sender.sendMessage("Already running that backend");
            return;
        }
        switch (nb) {
            case JSON:
                FactionsJSON.convertTo();
                break;
            default:
                this.sender.sendMessage("Invalid backend");
                return;
            
        }
        Conf.backEnd = nb;
    }

}
