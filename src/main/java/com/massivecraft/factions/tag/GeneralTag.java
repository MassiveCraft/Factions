package com.massivecraft.factions.tag;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

import java.util.function.Supplier;

public enum GeneralTag implements Tag {
    MAX_WARPS("{max-warps}", () -> String.valueOf(P.p.getConfig().getInt("max-warps", 5))),
    MAX_ALLIES("{max-allies}", () -> getRelation("ally")),
    MAX_ENEMIES("{max-enemies}", () -> getRelation("enemy")),
    MAX_TRUCES("{max-truces}", () -> getRelation("truce")),
    FACTIONLESS("{factionless}", () -> String.valueOf(Factions.getInstance().getNone().getFPlayersWhereOnline(true).size())),
    TOTAL_ONLINE("{total-online}", () -> String.valueOf(Bukkit.getOnlinePlayers().size())),
    ;

    private final String tag;
    private final Supplier<String> supplier;

    private static String getRelation(String relation) {
        if (P.p.getConfig().getBoolean("max-relations.enabled", true)) {
            return String.valueOf(P.p.getConfig().getInt("max-relations." + relation, 10));
        }
        return TL.GENERIC_INFINITY.toString();
    }

    public static String parse(String text) {
        for (GeneralTag tag : GeneralTag.values()) {
            text = tag.replace(text);
        }
        return text;
    }

    GeneralTag(String tag, Supplier<String> supplier) {
        this.tag = tag;
        this.supplier = supplier;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public boolean foundInString(String test) {
        return test != null && test.contains(this.tag);
    }

    public String replace(String text) {
        if (!this.foundInString(text)) {
            return text;
        }
        String result = this.supplier.get();
        return result == null ? null : text.replace(this.tag, result);
    }
}
