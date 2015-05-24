package com.massivecraft.factions.zcore.util;


import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.util.MiscUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

import static com.massivecraft.factions.zcore.util.TagReplacer.TagType;

public class TagUtil {

    private static final int ARBITRARY_LIMIT = 25000;

    /**
     * Replaces all variables in a plain raw line for a faction
     *
     * @param faction for faction
     * @param line    raw line from config with variables to replace for
     *
     * @return clean line
     */
    public static String parsePlain(Faction faction, String line) {
        for (TagReplacer tagReplacer : TagReplacer.getByType(TagType.FACTION)) {
            if (tagReplacer.contains(line)) {
                line = tagReplacer.replace(line, tagReplacer.getValue(faction, null));
            }
        }
        return line;
    }

    /**
     * Replaces all variables in a plain raw line for a player
     *
     * @param fplayer for player
     * @param line    raw line from config with variables to replace for
     *
     * @return clean line
     */
    public static String parsePlain(FPlayer fplayer, String line) {
        for (TagReplacer tagReplacer : TagReplacer.getByType(TagType.PLAYER)) {
            if (tagReplacer.contains(line)) {
                String rep = tagReplacer.getValue(fplayer.getFaction(), fplayer);
                if (rep == null) {
                    rep = ""; // this should work, but it's not a good way to handle whatever is going wrong
                }
                line = tagReplacer.replace(line, rep);
            }
        }
        return line;
    }

    /**
     * Replaces all variables in a plain raw line for a faction, using relations from fplayer
     *
     * @param faction for faction
     * @param fplayer from player
     * @param line    raw line from config with variables to replace for
     *
     * @return clean line
     */
    public static String parsePlain(Faction faction, FPlayer fplayer, String line) {
        for (TagReplacer tagReplacer : TagReplacer.getByType(TagType.PLAYER)) {
            if (tagReplacer.contains(line)) {
                line = tagReplacer.replace(line, tagReplacer.getValue(faction, fplayer));
            }
        }
        return line;
    }


    /**
     * Scan a line and parse the component variable into a component list
     *
     * @param faction for faction (viewers faction)
     * @param fme     for player (viewer)
     * @param line    component prefix
     *
     * @return
     */
    public static List<BaseComponent> parseComponent(Faction faction, FPlayer fme, String line) {
        for (TagReplacer tagReplacer : TagReplacer.getByType(TagType.FANCY)) {
            if (tagReplacer.contains(line)) {
                String clean = line.replace(tagReplacer.getTag(), ""); // remove tag
                return getComponent(faction, fme, tagReplacer, clean);
            }
        }
        return null;
    }

    /**
     * Checks if a line has component variables
     *
     * @param line raw line from config with variables
     *
     * @return if the line has fancy variables
     */
    public static boolean hasComponent(String line) {
        for (TagReplacer tagReplacer : TagReplacer.getByType(TagType.FANCY)) {
            if (tagReplacer.contains(line)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Lets get components?
     *
     * @param target Faction to get relate from
     * @param fme    Player to relate to
     * @param prefix First part of the fancy message
     *
     * @return list of fancy messages to send
     */
    protected static List<BaseComponent> getComponent(Faction target, FPlayer fme, TagReplacer type, String prefix) {
        List<BaseComponent> components = new ArrayList<BaseComponent>();
        switch (type) {
            case ALLIES_LIST:
                BaseComponent currentAllies = P.p.txt.parseComponent(prefix);
                boolean firstAlly = true;
                for (Faction otherFaction : Factions.getInstance().getAllFactions()) {
                    if (otherFaction == target) {
                        continue;
                    }
                    String s = otherFaction.getTag(fme);
                    if (otherFaction.getRelationTo(target).isAlly()) {
                        TextComponent then = new TextComponent(firstAlly ? s : ", " + s);
                        List<String> list = tipFaction(otherFaction);
                        TextComponent[] hover = new TextComponent[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            TextComponent component = new TextComponent(list.get(i));
                            component.setColor(net.md_5.bungee.api.ChatColor.valueOf(fme.getColorTo(otherFaction).name()));
                            hover[i] = component;
                        }
                        then.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
                        currentAllies.addExtra(then);
                        firstAlly = false;
                        if (currentAllies.toPlainText().length() > ARBITRARY_LIMIT) {
                            components.add(currentAllies);
                            currentAllies = new TextComponent("");
                        }
                    }
                }
                components.add(currentAllies);
                return components; // we must return here and not outside the switch
            case ENEMIES_LIST:
                BaseComponent currentEnemies = P.p.txt.parseComponent(prefix);
                boolean firstEnemy = true;
                for (Faction otherFaction : Factions.getInstance().getAllFactions()) {
                    if (otherFaction == target) {
                        continue;
                    }
                    String s = otherFaction.getTag(fme);
                    if (otherFaction.getRelationTo(target).isEnemy()) {
                        TextComponent then = new TextComponent(firstEnemy ? s : ", " + s);
                        List<String> list = tipFaction(otherFaction);
                        TextComponent[] hover = new TextComponent[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            TextComponent component = new TextComponent(list.get(i));
                            component.setColor(net.md_5.bungee.api.ChatColor.valueOf(fme.getColorTo(otherFaction).name()));
                            hover[i] = component;
                        }
                        then.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
                        currentEnemies.addExtra(then);
                        firstEnemy = false;
                        if (currentEnemies.toPlainText().length() > ARBITRARY_LIMIT) {
                            components.add(currentEnemies);
                            currentEnemies = new TextComponent("");
                        }
                    }
                }
                components.add(currentEnemies);
                return components; // we must return here and not outside the switch
            case ONLINE_LIST:
                BaseComponent currentOnline = P.p.txt.parseComponent(prefix);
                boolean firstOnline = true;
                for (FPlayer p : MiscUtil.rankOrder(target.getFPlayersWhereOnline(true))) {
                    String name = p.getNameAndTitle();
                    TextComponent then = new TextComponent(firstOnline ? name : ", " + name);
                    List<String> list = tipPlayer(p);
                    TextComponent[] hover = new TextComponent[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        TextComponent component = new TextComponent(list.get(i));
                        component.setColor(net.md_5.bungee.api.ChatColor.valueOf(fme.getColorTo(p).name()));
                        hover[i] = component;
                    }
                    then.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
                    currentOnline.addExtra(then);
                    firstOnline = false;
                    if (currentOnline.toPlainText().length() > ARBITRARY_LIMIT) {
                        components.add(currentOnline);
                        currentOnline = new TextComponent("");
                    }
                }
                components.add(currentOnline);
                return components; // we must return here and not outside the switch
            case OFFLINE_LIST:
                BaseComponent currentOffline = P.p.txt.parseComponent(prefix);
                boolean firstOffline = true;
                for (FPlayer p : MiscUtil.rankOrder(target.getFPlayers())) {
                    String name = p.getNameAndTitle();
                    if (!p.isOnline()) {
                        TextComponent then = new TextComponent(firstOffline ? name : ", " + name);
                        List<String> list = tipPlayer(p);
                        TextComponent[] hover = new TextComponent[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            TextComponent component = new TextComponent(list.get(i));
                            component.setColor(net.md_5.bungee.api.ChatColor.valueOf(fme.getColorTo(p).name()));
                            hover[i] = component;
                        }
                        then.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
                        currentOffline.addExtra(then);
                        firstOffline = false;
                        if (currentOffline.toPlainText().length() > ARBITRARY_LIMIT) {
                            components.add(currentOffline);
                            currentOffline = new TextComponent("");
                        }
                    }
                }
                components.add(currentOffline);
                return components; // we must return here and not outside the switch
        }
        return null;
    }

    /**
     * Parses tooltip variables from config <br> Supports variables for factions only (type 2)
     *
     * @param faction faction to tooltip for
     *
     * @return list of tooltips for a fancy message
     */
    private static List<String> tipFaction(Faction faction) {
        List<String> lines = new ArrayList<String>();
        for (String line : P.p.getConfig().getStringList("tooltips.list")) {
            lines.add(ChatColor.translateAlternateColorCodes('&', TagUtil.parsePlain(faction, line)));
        }
        return lines;
    }

    /**
     * Parses tooltip variables from config <br> Supports variables for players and factions (types 1 and 2)
     *
     * @param fplayer player to tooltip for
     *
     * @return list of tooltips for a fancy message
     */
    private static List<String> tipPlayer(FPlayer fplayer) {
        List<String> lines = new ArrayList<String>();
        for (String line : P.p.getConfig().getStringList("tooltips.show")) {
            lines.add(ChatColor.translateAlternateColorCodes('&', TagUtil.parsePlain(fplayer, line)));
        }
        return lines;
    }
}
