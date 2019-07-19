package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.zcore.util.TL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;


public class CmdList extends FCommand {

    private String[] defaults = new String[3];

    public CmdList() {
        super();
        this.aliases.add("list");
        this.aliases.add("ls");

        // default values in case user has old config
        defaults[0] = "&e&m----------&r&e[ &2Faction List &9{pagenumber}&e/&9{pagecount} &e]&m----------";
        defaults[1] = "<i>Factionless<i> {factionless} online";
        defaults[2] = "<a>{faction} <i>{online} / {members} online, <a>Land / Power / Maxpower: <i>{chunks}/{power}/{maxPower}";

        this.optionalArgs.put("page", "1");

        this.requirements = new CommandRequirements.Builder(Permission.LIST).build();
    }

    @Override
    public void perform(CommandContext context) {
        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!context.payForCommand(Conf.econCostList, "to list the factions", "for listing the factions")) {
            return;
        }

        ArrayList<Faction> factionList = Factions.getInstance().getAllFactions();
        factionList.remove(Factions.getInstance().getNone());
        factionList.remove(Factions.getInstance().getSafeZone());
        factionList.remove(Factions.getInstance().getWarZone());

        // remove exempt factions
        if (!context.sender.hasPermission("factions.show.bypassexempt")) {
            List<String> exemptFactions = P.p.getConfig().getStringList("show-exempt");
            factionList.removeIf(next -> exemptFactions.contains(next.getTag()));
        }

        // Sort by total followers first
        factionList.sort(this.compare(Faction::getFPlayers));

        // Then sort by how many members are online now
        factionList.sort(this.compare(f -> f.getFPlayersWhereOnline(true)));

        ArrayList<String> lines = new ArrayList<>();

        factionList.add(0, Factions.getInstance().getNone());

        final int pageheight = 9;
        int pagenumber = context.argAsInt(0, 1);
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


        String header = p.getConfig().getString("list.header", defaults[0]);
        header = header.replace("{pagenumber}", String.valueOf(pagenumber)).replace("{pagecount}", String.valueOf(pagecount));
        lines.add(p.txt.parse(header));

        for (Faction faction : factionList.subList(start, end)) {
            if (faction.isWilderness()) {
                lines.add(p.txt.parse(Tag.parsePlain(faction, p.getConfig().getString("list.factionless", defaults[1]))));
                continue;
            }
            lines.add(p.txt.parse(Tag.parsePlain(faction, context.fPlayer, p.getConfig().getString("list.entry", defaults[2]))));
        }
        context.sendMessage(lines);
    }

    private Comparator<Faction> compare(Function<Faction, ? extends Collection<?>> func) {
        return (f1, f2) -> {
            int f1Size = func.apply(f1).size();
            int f2Size = func.apply(f2).size();
            if (f1Size < f2Size) {
                return 1;
            } else if (f1Size > f2Size) {
                return -1;
            }
            return 0;
        };
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_LIST_DESCRIPTION;
    }
}