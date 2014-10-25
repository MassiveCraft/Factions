package com.massivecraft.factions.scoreboards.sidebar;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.scoreboards.FSidebarProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.ListIterator;

public class FDefaultSidebar extends FSidebarProvider {

    @Override
    public String getTitle(FPlayer fplayer) {
        return ChatColor.translateAlternateColorCodes('&', P.p.getConfig().getString("scoreboard.default-title", "i love drt"));
    }

    @Override
    public List<String> getLines(FPlayer fplayer) {
        List<String> lines = P.p.getConfig().getStringList("scoreboard.default");

        ListIterator<String> it = lines.listIterator();
        while (it.hasNext()) {
            it.set(replace(fplayer, it.next()));
        }
        return lines;
    }

    private String replace(FPlayer fplayer, String s) {
        String faction = !fplayer.getFaction().isNone() ? fplayer.getFaction().getTag() : "factionless";
        s = s.replace("{name}", fplayer.getName())
             .replace("{power}", String.valueOf(fplayer.getPowerRounded()))
             .replace("{balance}", String.valueOf(Econ.getFriendlyBalance(fplayer.getPlayer().getUniqueId())))
             .replace("{faction}", faction)
             .replace("{maxPower}", String.valueOf(fplayer.getPowerMaxRounded()))
             .replace("{totalOnline}", String.valueOf(Bukkit.getServer().getOnlinePlayers().length));
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
