package com.massivecraft.factions.scoreboards.sidebar;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.scoreboards.FSidebarProvider;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.ListIterator;

public class FInfoSidebar extends FSidebarProvider {
    private final Faction faction;

    public FInfoSidebar(Faction faction) {
        this.faction = faction;
    }

    @Override
    public String getTitle(FPlayer fplayer) {
        return faction.getRelationTo(fplayer).getColor() + faction.getTag();
    }

    @Override
    public List<String> getLines(FPlayer fplayer) {
        List<String> lines = P.p.getConfig().getStringList("scoreboard.finfo");

        ListIterator<String> it = lines.listIterator();
        while (it.hasNext()) {
            it.set(replaceFInfoTags(it.next()));
        }

        return lines;
    }

    private String replaceFInfoTags(String s) {
        boolean raidable = faction.getLandRounded() > faction.getPower();
        FPlayer fLeader = faction.getFPlayerAdmin();
        String leader = fLeader == null ? "Server" : fLeader.getName().substring(0, fLeader.getName().length() > 14 ? 13 : fLeader.getName().length());
        return ChatColor.translateAlternateColorCodes('&', s.replace("{power}", String.valueOf(faction.getPowerRounded()))
                                                            .replace("{online}", String.valueOf(faction.getOnlinePlayers().size()))
                                                            .replace("{members}", String.valueOf(faction.getFPlayers().size()))
                                                            .replace("{leader}", leader)
                                                            .replace("{chunks}", String.valueOf(faction.getLandRounded()))
                                                            .replace("{raidable}", String.valueOf(raidable)));
    }
}
