package com.massivecraft.factions.scoreboards;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TagUtil;

import java.util.List;

public abstract class FSidebarProvider {

    public abstract String getTitle(FPlayer fplayer);

    public abstract List<String> getLines(FPlayer fplayer);

    public String replaceTags(FPlayer fPlayer, String s) {
        s = TagUtil.parsePlaceholders(fPlayer.getPlayer(), s);

        return qualityAssure(TagUtil.parsePlain(fPlayer, s));
    }

    public String replaceTags(Faction faction, FPlayer fPlayer, String s) {
        // Run through Placeholder API first
        s = TagUtil.parsePlaceholders(fPlayer.getPlayer(), s);

        return qualityAssure(TagUtil.parsePlain(faction, fPlayer, s));
    }

    private String qualityAssure(String line) {
        if (line.contains("{notFrozen}") || line.contains("{notPermanent}")) {
            return "n/a"; // we dont support support these error variables in scoreboards
        }
        if (line.contains("{ig}")) {
            // since you can't really fit a whole "Faction Home: world, x, y, z" on one line
            // we assume it's broken up into two lines, so returning our tl will suffice.
            return TL.COMMAND_SHOW_NOHOME.toString();
        }
        return P.p.txt.parse(line); // finally add color :)
    }
}