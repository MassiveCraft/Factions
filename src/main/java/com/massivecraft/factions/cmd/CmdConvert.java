package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Conf.Backend;
import com.massivecraft.factions.zcore.persist.json.FactionsJSON;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.command.ConsoleCommandSender;

public class CmdConvert extends FCommand {

    public CmdConvert() {
        this.aliases.add("convert");

        this.requiredArgs.add("[MYSQL|JSON]");
    }

    @Override
    public void perform(CommandContext context) {
        if (!(context.sender instanceof ConsoleCommandSender)) {
            context.sender.sendMessage(TL.GENERIC_CONSOLEONLY.toString());
        }
        Backend nb = Backend.valueOf(context.argAsString(0).toUpperCase());
        if (nb == Conf.backEnd) {
            context.sender.sendMessage(TL.COMMAND_CONVERT_BACKEND_RUNNING.toString());
            return;
        }
        switch (nb) {
            case JSON:
                FactionsJSON.convertTo();
                break;
            default:
                context.sender.sendMessage(TL.COMMAND_CONVERT_BACKEND_INVALID.toString());
                return;

        }
        Conf.backEnd = nb;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CONVERT_DESCRIPTION;
    }

}
