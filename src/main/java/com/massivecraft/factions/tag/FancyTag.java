package com.massivecraft.factions.tag;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.QuadFunction;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

import java.util.*;

public enum FancyTag implements Tag {
    ALLIES_LIST("{allies-list}", (target, fme, prefix, gm) -> processRelation(prefix, target, fme, Relation.ALLY)),
    ENEMIES_LIST("{enemies-list}", (target, fme, prefix, gm) -> processRelation(prefix, target, fme, Relation.ENEMY)),
    TRUCES_LIST("{truces-list}", (target, fme, prefix, gm) -> processRelation(prefix, target, fme, Relation.TRUCE)),
    ONLINE_LIST("{online-list}", (target, fme, prefix, gm) -> {
        List<FancyMessage> fancyMessages = new ArrayList<>();
        FancyMessage currentOnline = P.p.txt.parseFancy(prefix);
        boolean firstOnline = true;
        for (FPlayer p : MiscUtil.rankOrder(target.getFPlayersWhereOnline(true, fme))) {
            if (fme.getPlayer() != null && !fme.getPlayer().canSee(p.getPlayer())) {
                continue; // skip
            }
            String name = p.getNameAndTitle();
            currentOnline.then(firstOnline ? name : ", " + name);
            currentOnline.tooltip(tipPlayer(p, gm)).color(fme.getColorTo(p));
            firstOnline = false;
            if (currentOnline.toJSONString().length() > ARBITRARY_LIMIT) {
                fancyMessages.add(currentOnline);
                currentOnline = new FancyMessage("");
            }
        }
        fancyMessages.add(currentOnline);
        return firstOnline && Tag.isMinimalShow() ? null : fancyMessages;
    }),
    OFFLINE_LIST("{offline-list}", (target, fme, prefix, gm) -> {
        List<FancyMessage> fancyMessages = new ArrayList<>();
        FancyMessage currentOffline = P.p.txt.parseFancy(prefix);
        boolean firstOffline = true;
        for (FPlayer p : MiscUtil.rankOrder(target.getFPlayers())) {
            String name = p.getNameAndTitle();
            // Also make sure to add players that are online BUT can't be seen.
            if (!p.isOnline() || (fme.getPlayer() != null && p.isOnline() && !fme.getPlayer().canSee(p.getPlayer()))) {
                currentOffline.then(firstOffline ? name : ", " + name);
                currentOffline.tooltip(tipPlayer(p, gm)).color(fme.getColorTo(p));
                firstOffline = false;
                if (currentOffline.toJSONString().length() > ARBITRARY_LIMIT) {
                    fancyMessages.add(currentOffline);
                    currentOffline = new FancyMessage("");
                }
            }
        }
        fancyMessages.add(currentOffline);
        return firstOffline && Tag.isMinimalShow() ? null : fancyMessages;
    }),
    ;

    private final String tag;
    private final QuadFunction<Faction, FPlayer, String, Map<UUID, String>, List<FancyMessage>> function;

    private static List<FancyMessage> processRelation(String prefix, Faction faction, FPlayer fPlayer, Relation relation) {
        List<FancyMessage> fancyMessages = new ArrayList<>();
        FancyMessage message = P.p.txt.parseFancy(prefix);
        boolean first = true;
        for (Faction otherFaction : Factions.getInstance().getAllFactions()) {
            if (otherFaction == faction) {
                continue;
            }
            String s = otherFaction.getTag(fPlayer);
            if (otherFaction.getRelationTo(faction) == relation) {
                message.then(first ? s : ", " + s);
                message.tooltip(tipFaction(otherFaction, fPlayer)).color(fPlayer.getColorTo(otherFaction));
                first = false;
                if (message.toJSONString().length() > ARBITRARY_LIMIT) {
                    fancyMessages.add(message);
                    message = new FancyMessage("");
                }
            }
        }
        fancyMessages.add(message);
        return first && Tag.isMinimalShow() ? null : fancyMessages;
    }

    public static List<FancyMessage> parse(String text, Faction faction, FPlayer player, Map<UUID, String> groupMap) {
        for (FancyTag tag : FancyTag.values()) {
            if (tag.foundInString(text)) {
                return tag.getMessage(text, faction, player, groupMap);
            }
        }
        return Collections.EMPTY_LIST; // We really shouldn't be here.
    }

    public static boolean anyMatch(String text) {
        for (FancyTag tag : FancyTag.values()) {
            if (tag.foundInString(text)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses tooltip variables from config <br> Supports variables for factions only (type 2)
     *
     * @param faction faction to tooltip for
     * @return list of tooltips for a fancy message
     */
    private static List<String> tipFaction(Faction faction, FPlayer player) {
        List<String> lines = new ArrayList<>();
        for (String line : P.p.getConfig().getStringList("tooltips.list")) {
            String string = Tag.parsePlain(faction, player, line);
            if (string == null) {
                continue;
            }
            lines.add(ChatColor.translateAlternateColorCodes('&', string));
        }
        return lines;
    }

    /**
     * Parses tooltip variables from config <br> Supports variables for players and factions (types 1 and 2)
     *
     * @param fplayer player to tooltip for
     * @return list of tooltips for a fancy message
     */
    private static List<String> tipPlayer(FPlayer fplayer, Map<UUID, String> groupMap) {
        List<String> lines = new ArrayList<>();
        for (String line : P.p.getConfig().getStringList("tooltips.show")) {
            String newLine = line;
            everythingOnYourWayOut:
            if (line.contains("{group}")) {
                if (groupMap != null) {
                    String group = groupMap.get(UUID.fromString(fplayer.getId()));
                    if (!group.trim().isEmpty()) {
                        newLine = newLine.replace("{group}", group);
                        break everythingOnYourWayOut;
                    }
                }
                continue;
            }
            String string = Tag.parsePlain(fplayer, newLine);
            if (string == null) {
                continue;
            }
            lines.add(ChatColor.translateAlternateColorCodes('&', string));
        }
        return lines;
    }

    FancyTag(String tag, QuadFunction<Faction, FPlayer, String, Map<UUID, String>, List<FancyMessage>> function) {
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

    public List<FancyMessage> getMessage(String text, Faction faction, FPlayer player, Map<UUID, String> groupMap) {
        if (!this.foundInString(text)) {
            return Collections.EMPTY_LIST; // We really, really shouldn't be here.
        }
        return this.function.apply(faction, player, text.replace(this.getTag(), ""), groupMap);
    }
}
