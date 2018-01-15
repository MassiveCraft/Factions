package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;

public class CmdStatus extends FCommand {

    public CmdStatus() {
        super();
        this.aliases.add("status");
        this.aliases.add("s");

        this.permission = Permission.STATUS.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        ArrayList<String> ret = new ArrayList<>();
        for (FPlayer fp : myFaction.getFPlayers()) {
            String humanized = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - fp.getLastLoginTime(), true, true) + TL.COMMAND_STATUS_AGOSUFFIX;
            String last = fp.isOnline() ? ChatColor.GREEN + TL.COMMAND_STATUS_ONLINE.toString() : (System.currentTimeMillis() - fp.getLastLoginTime() < 432000000 ? ChatColor.YELLOW + humanized : ChatColor.RED + humanized);
            String power = ChatColor.YELLOW + String.valueOf(fp.getPowerRounded()) + " / " + String.valueOf(fp.getPowerMaxRounded()) + ChatColor.RESET;
            ret.add(String.format(TL.COMMAND_STATUS_FORMAT.toString(), ChatColor.GOLD + fp.getRole().getPrefix() + fp.getName() + ChatColor.RESET, power, last).trim());
        }
        fme.sendMessage(ret);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STATUS_DESCRIPTION;
    }

}
