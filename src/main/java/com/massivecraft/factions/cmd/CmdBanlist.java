package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.BanInfo;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

import java.util.ArrayList;
import java.util.List;

public class CmdBanlist extends FCommand {

    public CmdBanlist() {
        super();
        this.aliases.add("banlist");
        this.aliases.add("bans");
        this.aliases.add("banl");

        this.optionalArgs.put("faction", "faction");

        this.permission = Permission.BAN.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction target = myFaction;
        if (!args.isEmpty()) {
            target = argAsFaction(0);
        }

        if (target == Factions.getInstance().getWilderness()) {
            sender.sendMessage(TL.COMMAND_BANLIST_NOFACTION.toString());
            return;
        }

        if (target == null) {
            sender.sendMessage(TL.COMMAND_BANLIST_INVALID.format(argAsString(0)));
            return;
        }

        List<String> lines = new ArrayList<>();
        lines.add(TL.COMMAND_BANLIST_HEADER.format(target.getBannedPlayers().size(), target.getTag(myFaction)));
        int i = 1;

        for (BanInfo info : target.getBannedPlayers()) {
            FPlayer banned = FPlayers.getInstance().getById(info.getBanned());
            FPlayer banner = FPlayers.getInstance().getById(info.getBanner());
            String timestamp = TL.sdf.format(info.getTime());

            lines.add(TL.COMMAND_BANLIST_ENTRY.format(i, banned.getName(), banner.getName(), timestamp));
            i++;
        }

        for (String s : lines) {
            fme.sendMessage(s);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_BANLIST_DESCRIPTION;
    }
}
