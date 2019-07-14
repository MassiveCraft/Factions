package com.massivecraft.factions.tag;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TL;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum FactionTag implements Tag {
    HOME_X("{x}", (fac) -> fac.hasHome() ? String.valueOf(fac.getHome().getBlockX()) : Tag.isMinimalShow() ? null : "{ig}"),
    HOME_Y("{y}", (fac) -> fac.hasHome() ? String.valueOf(fac.getHome().getBlockY()) : Tag.isMinimalShow() ? null : "{ig}"),
    HOME_Z("{z}", (fac) -> fac.hasHome() ? String.valueOf(fac.getHome().getBlockZ()) : Tag.isMinimalShow() ? null : "{ig}"),
    CHUNKS("{chunks}", (fac) -> String.valueOf(fac.getLandRounded())),
    WARPS("{warps}", (fac) -> String.valueOf(fac.getWarps().size())),
    HEADER("{header}", (fac, fp) -> P.p.txt.titleize(fac.getTag(fp))),
    POWER("{power}", (fac) -> String.valueOf(fac.getPowerRounded())),
    MAX_POWER("{maxPower}", (fac) -> String.valueOf(fac.getPowerMaxRounded())),
    POWER_BOOST("{power-boost}", (fac) -> {
        double powerBoost = fac.getPowerBoost();
        return (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? TL.COMMAND_SHOW_BONUS.toString() : TL.COMMAND_SHOW_PENALTY.toString() + powerBoost + ")");
    }),
    LEADER("{leader}", (fac) -> {
        FPlayer fAdmin = fac.getFPlayerAdmin();
        return fAdmin == null ? "Server" : fAdmin.getName().substring(0, fAdmin.getName().length() > 14 ? 13 : fAdmin.getName().length());
    }),
    JOINING("{joining}", (fac) -> (fac.getOpen() ? TL.COMMAND_SHOW_UNINVITED.toString() : TL.COMMAND_SHOW_INVITATION.toString())),
    FACTION("{faction}", (Function<Faction, String>) Faction::getTag),
    FACTION_RELATION_COLOR("{faction-relation-color}", (fac, fp) -> fp == null ? "" : fp.getColorTo(fac).toString()),
    HOME_WORLD("{world}", (fac) -> fac.hasHome() ? fac.getHome().getWorld().getName() : Tag.isMinimalShow() ? null : "{ig}"),
    RAIDABLE("{raidable}", (fac) -> {
        if (P.p.getConfig().getBoolean("hcf.raidable", false)) {
            boolean raidable = fac.getLandRounded() >= fac.getPowerRounded();
            String str = raidable ? TL.RAIDABLE_TRUE.toString() : TL.RAIDABLE_FALSE.toString();
            if (P.p.getConfig().getBoolean("hcf.dtr", false)) {
                int dtr = raidable ? 0 : (int) Math.ceil(((double) (fac.getPowerRounded() - fac.getLandRounded())) / Conf.powerPerDeath);
                str += ' ' + TL.COMMAND_SHOW_DEATHS_TIL_RAIDABLE.format(dtr);
            }
            return str;
        }
        return null;
    }),
    PEACEFUL("{peaceful}", (fac) -> fac.isPeaceful() ? Conf.colorNeutral + TL.COMMAND_SHOW_PEACEFUL.toString() : ""),
    PERMANENT("permanent", (fac) -> fac.isPermanent() ? "permanent" : "{notPermanent}"), // no braces needed
    LAND_VALUE("{land-value}", (fac) -> Econ.shouldBeUsed() ? Econ.moneyString(Econ.calculateTotalLandValue(fac.getLandRounded())) : Tag.isMinimalShow() ? null : TL.ECON_OFF.format("value")),
    DESCRIPTION("{description}", Faction::getDescription),
    CREATE_DATE("{create-date}", (fac) -> TL.sdf.format(fac.getFoundedDate())),
    LAND_REFUND("{land-refund}", (fac) -> Econ.shouldBeUsed() ? Econ.moneyString(Econ.calculateTotalLandRefund(fac.getLandRounded())) : Tag.isMinimalShow() ? null : TL.ECON_OFF.format("refund")),
    BANK_BALANCE("{faction-balance}", (fac) -> {
        if (Econ.shouldBeUsed()) {
            return Conf.bankEnabled ? Econ.moneyString(Econ.getBalance(fac.getAccountId())) : Tag.isMinimalShow() ? null : TL.ECON_OFF.format("balance");
        }
        return Tag.isMinimalShow() ? null : TL.ECON_OFF.format("balance");
    }),
    ALLIES_COUNT("{allies}", (fac) -> String.valueOf(fac.getRelationCount(Relation.ALLY))),
    ENEMIES_COUNT("{enemies}", (fac) -> String.valueOf(fac.getRelationCount(Relation.ENEMY))),
    TRUCES_COUNT("{truces}", (fac) -> String.valueOf(fac.getRelationCount(Relation.TRUCE))),
    ONLINE_COUNT("{online}", (fac, fp) -> {
        if (fp != null && fp.isOnline()) {
            return String.valueOf(fac.getFPlayersWhereOnline(true, fp).size());
        } else {
            // Only console should ever get here.
            return String.valueOf(fac.getFPlayers().size());
        }
    }),
    OFFLINE_COUNT("{offline}", (fac) -> String.valueOf(fac.getFPlayers().size() - fac.getOnlinePlayers().size())),
    FACTION_SIZE("{members}", (fac) -> String.valueOf(fac.getFPlayers().size())),
    FACTION_KILLS("{faction-kills}", (fac) -> String.valueOf(fac.getKills())),
    FACTION_DEATHS("{faction-deaths}", (fac) -> String.valueOf(fac.getDeaths())),
    FACTION_BANCOUNT("{faction-bancount}", (fac) -> String.valueOf(fac.getBannedPlayers().size())),
    ;

    private final String tag;
    private final BiFunction<Faction, FPlayer, String> biFunction;
    private final Function<Faction, String> function;

    public static String parse(String text, Faction faction, FPlayer player) {
        for (FactionTag tag : FactionTag.values()) {
            text = tag.replace(text, faction, player);
        }
        return text;
    }

    public static String parse(String text, Faction faction) {
        for (FactionTag tag : FactionTag.values()) {
            text = tag.replace(text, faction);
        }
        return text;
    }

    FactionTag(String tag, BiFunction<Faction, FPlayer, String> function) {
        this.tag = tag;
        this.biFunction = function;
        this.function = null;
    }

    FactionTag(String tag, Function<Faction, String> function) {
        this.tag = tag;
        this.biFunction = null;
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

    public String replace(String text, Faction faction, FPlayer player) {
        if (!this.foundInString(text)) {
            return text;
        }
        if (this.biFunction == null) {
            return this.replace(text, faction);
        }
        String result = this.biFunction.apply(faction, player);
        return result == null ? null : text.replace(this.tag, result);
    }

    public String replace(String text, Faction faction) {
        if (this.function == null || !this.foundInString(text)) {
            return text;
        }
        String result = this.function.apply(faction);
        return result == null ? null : text.replace(this.tag, result);
    }
}
