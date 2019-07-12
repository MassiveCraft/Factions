package com.massivecraft.factions.tag;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.zcore.util.TL;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;

import java.util.function.Function;

public enum PlayerTag implements Tag {
    // PLAYER_GROUP("{group}"), TODO deal with this specialcase
    LAST_SEEN("{lastSeen}", (fp) -> {
        String humanized = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - fp.getLastLoginTime(), true, true) + TL.COMMAND_STATUS_AGOSUFFIX;
        return fp.isOnline() ? ChatColor.GREEN + TL.COMMAND_STATUS_ONLINE.toString() : (System.currentTimeMillis() - fp.getLastLoginTime() < 432000000 ? ChatColor.YELLOW + humanized : ChatColor.RED + humanized);
    }),
    PLAYER_BALANCE("{balance}", (fp) -> Econ.isSetup() ? Econ.getFriendlyBalance(fp) : TL.ECON_OFF.format("balance")),
    PLAYER_POWER("{player-power}", (fp) -> String.valueOf(fp.getPowerRounded())),
    PLAYER_MAXPOWER("{player-maxpower}", (fp) -> String.valueOf(fp.getPowerMaxRounded())),
    PLAYER_KILLS("{player-kills}", (fp) -> String.valueOf(fp.getKills())),
    PLAYER_DEATHS("{player-deaths}", (fp) -> String.valueOf(fp.getDeaths())),
    PLAYER_NAME("{name}", FPlayer::getName),
    ;

    private final String tag;
    private final Function<FPlayer, String> function;

    public static String parse(String text, FPlayer player) {
        for (PlayerTag tag : PlayerTag.values()) {
            text = tag.replace(text, player);
        }
        return text;
    }

    PlayerTag(String tag, Function<FPlayer, String> function) {
        this.tag = tag;
        this.function = function;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public boolean foundInString(String test) {
        return test != null && test.contains(this.tag);
    }

    public String replace(String text, FPlayer player) {
        if (!this.foundInString(text)) {
            return text;
        }
        String result = this.function.apply(player);
        return result == null ? text : text.replace(this.tag, result);
    }
}
