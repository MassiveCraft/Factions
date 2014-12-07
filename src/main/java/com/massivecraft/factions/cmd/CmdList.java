package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

import mkremins.fanciful.FancyMessage;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class CmdList extends FCommand {

    public CmdList() {
        super();
        this.aliases.add("list");
        this.aliases.add("ls");

        //this.requiredArgs.add("");
        this.optionalArgs.put("page", "1");

        this.permission = Permission.LIST.node;
        this.disableOnLock = false;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!payForCommand(Conf.econCostList, TL.COMMAND_LIST_TOLIST.toString() , TL.COMMAND_LIST_FORLIST.toString())) {
            return;
        }

        ArrayList<Faction> factionList = Factions.getInstance().getAllFactions();
        factionList.remove(Factions.getInstance().getNone());
        factionList.remove(Factions.getInstance().getSafeZone());
        factionList.remove(Factions.getInstance().getWarZone());

        // Sort by total followers first
        Collections.sort(factionList, new Comparator<Faction>() {
            @Override
            public int compare(Faction f1, Faction f2) {
                int f1Size = f1.getFPlayers().size();
                int f2Size = f2.getFPlayers().size();
                if (f1Size < f2Size) {
                    return 1;
                } else if (f1Size > f2Size) {
                    return -1;
                }
                return 0;
            }
        });

        // Then sort by how many members are online now
        Collections.sort(factionList, new Comparator<Faction>() {
            @Override
            public int compare(Faction f1, Faction f2) {
                int f1Size = f1.getFPlayersWhereOnline(true).size();
                int f2Size = f2.getFPlayersWhereOnline(true).size();
                if (f1Size < f2Size) {
                    return 1;
                } else if (f1Size > f2Size) {
                    return -1;
                }
                return 0;
            }
        });

        ArrayList<FancyMessage> lines = new ArrayList<FancyMessage>();

        factionList.add(0, Factions.getInstance().getNone());

        final int pageheight = 9;
        int pagenumber = this.argAsInt(0, 1);
        int pagecount = (factionList.size() / pageheight) + 1;
        if (pagenumber > pagecount) {
            pagenumber = pagecount;
        } else if (pagenumber < 1) {
            pagenumber = 1;
        }
        int start = (pagenumber - 1) * pageheight;
        int end = start + pageheight;
        if (end > factionList.size()) {
            end = factionList.size();
        }

        lines.add(new FancyMessage(p.txt.titleize(TL.COMMAND_LIST_FACTIONLIST.toString() + pagenumber + "/" + pagecount)));

        for (Faction faction : factionList.subList(start, end)) {
            if (faction.isNone()) {
                String online = String.valueOf(faction.getFPlayersWhereOnline(true).size());
                FancyMessage msg = new FancyMessage(TL.COMMAND_LIST_ONLINEFACTIONLESS.toString() + online).color(ChatColor.YELLOW);
                lines.add(msg);
                continue;
            }
            String online = " " + faction.getFPlayersWhereOnline(true).size() + "/" + faction.getFPlayers().size() + " online";

            FancyMessage msg = new FancyMessage(faction.getTag(fme) + online).color(ChatColor.YELLOW).tooltip(getToolTips(faction));
            lines.add(msg);
            //lines.add(p.txt.parse("%s<i> %d/%d online, %d/%d/%d", faction.getTag(fme), faction.getFPlayersWhereOnline(true).size(), faction.getFPlayers().size(), faction.getLandRounded(), faction.getPowerRounded(), faction.getPowerMaxRounded()));
        }

        sendFancyMessage(lines);
    }
}
