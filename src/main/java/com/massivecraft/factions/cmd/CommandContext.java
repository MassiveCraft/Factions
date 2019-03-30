package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.util.TL;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/*
    Object that contains information about a command being executed,
    args, player, faction
 */
public class CommandContext {

    public CommandSender sender;

    public Player player;
    public FPlayer fPlayer;
    public Faction faction;

    public List<String> args;
    public String alias;

    public CommandContext(CommandSender sender, List<String> args, String alias) {
        this.sender = sender;
        this.args = args;
        this.alias = alias;

        if (sender instanceof Player) {
            this.player = (Player) sender;
            this.fPlayer = FPlayers.getInstance().getByPlayer(player);
            this.faction = fPlayer.getFaction();
        }
    }

    // -------------------------------------------- //
    // Message Sending Helpers
    // -------------------------------------------- //

    public void msg(String str, Object... args) {
        sender.sendMessage(P.p.txt.parse(str, args));
    }

    public void msg(TL translation, Object... args) {
        sender.sendMessage(P.p.txt.parse(translation.toString(), args));
    }

    public void sendMessage(String msg) {
        sender.sendMessage(msg);
    }

    public void sendMessage(List<String> msgs) {
        for (String msg : msgs) {
            this.sendMessage(msg);
        }
    }

    public void sendFancyMessage(FancyMessage message) {
        message.send(sender);
    }

    public void sendFancyMessage(List<FancyMessage> messages) {
        for (FancyMessage m : messages) {
            sendFancyMessage(m);
        }
    }

    // TODO: Clean this UP
    // -------------------------------------------- //
    // Argument Readers
    // -------------------------------------------- //

    // Is set? ======================
    public boolean argIsSet(int idx) {
        return args.size() >= idx + 1;
    }

    // STRING ======================
    public String argAsString(int idx, String def) {
        if (args.size() < idx + 1) {
            return def;
        }
        return args.get(idx);
    }

    public String argAsString(int idx) {
        return argAsString(idx, null);
    }

    // INT ======================
    public Integer strAsInt(String str, Integer def) {
        if (str == null) {
            return def;
        }
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return def;
        }
    }

    public Integer argAsInt(int idx, Integer def) {
        return strAsInt(argAsString(idx), def);
    }

    public Integer argAsInt(int idx) {
        return argAsInt(idx, null);
    }

    // Double ======================
    public Double strAsDouble(String str, Double def) {
        if (str == null) {
            return def;
        }
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return def;
        }
    }

    public Double argAsDouble(int idx, Double def) {
        return strAsDouble(argAsString(idx), def);
    }

    public Double argAsDouble(int idx) {
        return argAsDouble(idx, null);
    }

    // TODO: Go through the str conversion for the other arg-readers as well.
    // Boolean ======================
    public Boolean strAsBool(String str) {
        str = str.toLowerCase();
        return str.startsWith("y") || str.startsWith("t") || str.startsWith("on") || str.startsWith("+") || str.startsWith("1");
    }

    public Boolean argAsBool(int idx, boolean def) {
        String str = argAsString(idx);
        if (str == null) {
            return def;
        }

        return strAsBool(str);
    }

    public Boolean argAsBool(int idx) {
        return argAsBool(idx, false);
    }

    // PLAYER ======================
    public Player strAsPlayer(String name, Player def, boolean msg) {
        Player ret = def;

        if (name != null) {
            Player player = Bukkit.getServer().getPlayer(name);
            if (player != null) {
                ret = player;
            }
        }

        if (msg && ret == null) {
            sender.sendMessage(TL.GENERIC_NOPLAYERFOUND.format(name));
        }

        return ret;
    }

    public Player argAsPlayer(int idx, Player def, boolean msg) {
        return this.strAsPlayer(argAsString(idx), def, msg);
    }

    public Player argAsPlayer(int idx, Player def) {
        return argAsPlayer(idx, def, true);
    }

    public Player argAsPlayer(int idx) {
        return argAsPlayer(idx, null);
    }

    // BEST PLAYER MATCH ======================
    public Player strAsBestPlayerMatch(String name, Player def, boolean msg) {
        Player ret = def;

        if (name != null) {
            List<Player> players = Bukkit.getServer().matchPlayer(name);
            if (players.size() > 0) {
                ret = players.get(0);
            }
        }

        if (msg && ret == null) {
            sender.sendMessage(TL.GENERIC_NOPLAYERMATCH.format(name));
        }

        return ret;
    }

    public Player argAsBestPlayerMatch(int idx, Player def, boolean msg) {
        return this.strAsBestPlayerMatch(argAsString(idx), def, msg);
    }

    public Player argAsBestPlayerMatch(int idx, Player def) {
        return argAsBestPlayerMatch(idx, def, true);
    }

    public Player argAsBestPlayerMatch(int idx) {
        return argAsPlayer(idx, null);
    }


    // -------------------------------------------- //
    // Faction Argument Readers
    // -------------------------------------------- //

    // FPLAYER ======================
    public FPlayer strAsFPlayer(String name, FPlayer def, boolean msg) {
        FPlayer ret = def;

        if (name != null) {
            for (FPlayer fplayer : FPlayers.getInstance().getAllFPlayers()) {
                if (fplayer.getName().equalsIgnoreCase(name)) {
                    ret = fplayer;
                    break;
                }
            }
        }

        if (msg && ret == null) {
            sender.sendMessage(TL.GENERIC_NOPLAYERFOUND.format(name));
        }

        return ret;
    }

    public FPlayer argAsFPlayer(int idx, FPlayer def, boolean msg) {
        return this.strAsFPlayer(argAsString(idx), def, msg);
    }

    public FPlayer argAsFPlayer(int idx, FPlayer def) {
        return argAsFPlayer(idx, def, true);
    }

    public FPlayer argAsFPlayer(int idx) {
        return argAsFPlayer(idx, null);
    }

    // BEST FPLAYER MATCH ======================
    public FPlayer strAsBestFPlayerMatch(String name, FPlayer def, boolean msg) {
        return strAsFPlayer(name, def, msg);
    }

    public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def, boolean msg) {
        return this.strAsBestFPlayerMatch(argAsString(idx), def, msg);
    }

    public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def) {
        return argAsBestFPlayerMatch(idx, def, true);
    }

    public FPlayer argAsBestFPlayerMatch(int idx) {
        return argAsBestFPlayerMatch(idx, null);
    }

    // FACTION ======================
    public Faction strAsFaction(String name, Faction def, boolean msg) {
        Faction ret = def;

        if (name != null) {
            // First we try an exact match
            Faction faction = Factions.getInstance().getByTag(name); // Checks for faction name match.

            // Now lets try for warzone / safezone. Helpful for custom warzone / safezone names.
            // Do this after we check for an exact match in case they rename the warzone / safezone
            // and a player created faction took one of the names.
            if (faction == null) {
                if (name.equalsIgnoreCase("warzone")) {
                    faction = Factions.getInstance().getWarZone();
                } else if (name.equalsIgnoreCase("safezone")) {
                    faction = Factions.getInstance().getSafeZone();
                }
            }

            // Next we match faction tags
            if (faction == null) {
                faction = Factions.getInstance().getBestTagMatch(name);
            }

            // Next we match player names
            if (faction == null) {
                FPlayer fplayer = strAsFPlayer(name, null, false);
                if (fplayer != null) {
                    faction = fplayer.getFaction();
                }
            }

            if (faction != null) {
                ret = faction;
            }
        }

        if (msg && ret == null) {
            sender.sendMessage(TL.GENERIC_NOFACTIONMATCH.format(name));
        }

        return ret;
    }

    public Faction argAsFaction(int idx, Faction def, boolean msg) {
        return this.strAsFaction(argAsString(idx), def, msg);
    }

    public Faction argAsFaction(int idx, Faction def) {
        return argAsFaction(idx, def, true);
    }

    public Faction argAsFaction(int idx) {
        return argAsFaction(idx, null);
    }

    /*
        Assertions
     */

    public boolean assertHasFaction() {
        if (player == null) {
            return true;
        }

        if (!fPlayer.hasFaction()) {
            sendMessage("You are not member of any faction.");
            return false;
        }
        return true;
    }

    public boolean assertMinRole(Role role) {
        if (player == null) {
            return true;
        }

        if (fPlayer.getRole().value < role.value) {
            msg("<b>You <h>must be " + role);
            return false;
        }
        return true;
    }

    /*
        Common Methods
    */
    public boolean canIAdministerYou(FPlayer i, FPlayer you) {
        if (!i.getFaction().equals(you.getFaction())) {
            i.sendMessage(P.p.txt.parse("%s <b>is not in the same faction as you.", you.describeTo(i, true)));
            return false;
        }

        if (i.getRole().value > you.getRole().value || i.getRole().equals(Role.ADMIN)) {
            return true;
        }

        if (you.getRole().equals(Role.ADMIN)) {
            i.sendMessage(P.p.txt.parse("<b>Only the faction admin can do that."));
        } else if (i.getRole().equals(Role.MODERATOR)) {
            if (i == you) {
                return true; //Moderators can control themselves
            } else {
                i.sendMessage(P.p.txt.parse("<b>Moderators can't control each other..."));
            }
        } else {
            i.sendMessage(P.p.txt.parse("<b>You must be a faction moderator to do that."));
        }

        return false;
    }

    // if economy is enabled and they're not on the bypass list, make 'em pay; returns true unless person can't afford the cost
    public boolean payForCommand(double cost, String toDoThis, String forDoingThis) {
        if (!Econ.shouldBeUsed() || this.fPlayer == null || cost == 0.0 || fPlayer.isAdminBypassing()) {
            return true;
        }

        if (Conf.bankEnabled && Conf.bankFactionPaysCosts && fPlayer.hasFaction()) {
            return Econ.modifyMoney(faction, -cost, toDoThis, forDoingThis);
        } else {
            return Econ.modifyMoney(fPlayer, -cost, toDoThis, forDoingThis);
        }
    }

    public boolean payForCommand(double cost, TL toDoThis, TL forDoingThis) {
        return payForCommand(cost, toDoThis.toString(), forDoingThis.toString());
    }

    // like above, but just make sure they can pay; returns true unless person can't afford the cost
    public boolean canAffordCommand(double cost, String toDoThis) {
        if (!Econ.shouldBeUsed() || fPlayer == null || cost == 0.0 || fPlayer.isAdminBypassing()) {
            return true;
        }

        if (Conf.bankEnabled && Conf.bankFactionPaysCosts && fPlayer.hasFaction()) {
            return Econ.hasAtLeast(faction, cost, toDoThis);
        } else {
            return Econ.hasAtLeast(fPlayer, cost, toDoThis);
        }
    }

    public void doWarmUp(WarmUpUtil.Warmup warmup, TL translationKey, String action, Runnable runnable, long delay) {
        this.doWarmUp(fPlayer, warmup, translationKey, action, runnable, delay);
    }

    public void doWarmUp(FPlayer player, WarmUpUtil.Warmup warmup, TL translationKey, String action, Runnable runnable, long delay) {
        WarmUpUtil.process(player, warmup, translationKey, action, runnable, delay);
    }


}
