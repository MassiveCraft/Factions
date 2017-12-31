package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

public class PlaceholderAPIManager extends EZPlaceholderHook {

    public PlaceholderAPIManager() {
        super(P.p, "factionsuuid");
    }

    @Override
    public String onPlaceholderRequest(Player player, String placeholder) {
        if(player == null || placeholder == null) {
            return "";
        }

        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        switch(placeholder) {
            case "faction":
                return fPlayer.getFaction().getTag();
            case "power":
                return String.valueOf(fPlayer.getPower());
        }

        return null;
    }
}
