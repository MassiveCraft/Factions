package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.MCommand;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.List;


public abstract class FCommand extends MCommand<P> {

    public SimpleDateFormat sdf = new SimpleDateFormat(TL.DATE_FORMAT.toString());

    public boolean disableOnLock;

    public FPlayer fme;
    public Faction myFaction;
    public boolean senderMustBeMember;
    public boolean senderMustBeModerator;
    public boolean senderMustBeAdmin;

    public boolean isMoneyCommand;

    public FCommand() {
        super(P.p);

        // Due to safety reasons it defaults to disable on lock.
        disableOnLock = true;

        // The money commands must be disabled if money should not be used.
        isMoneyCommand = false;

        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void execute(CommandSender sender, List<String> args, List<MCommand<?>> commandChain) {
        if (sender instanceof Player) {
            this.fme = FPlayers.getInstance().getByPlayer((Player) sender);
            this.myFaction = this.fme.getFaction();
        } else {
            this.fme = null;
            this.myFaction = null;
        }
        super.execute(sender, args, commandChain);
    }

    @Override
    public boolean isEnabled() {
        if (p.getLocked() && this.disableOnLock) {
            msg("<b>Factions was locked by an admin. Please try again later.");
            return false;
        }

        if (this.isMoneyCommand && !Conf.econEnabled) {
            msg("<b>Faction economy features are disabled on this server.");
            return false;
        }

        if (this.isMoneyCommand && !Conf.bankEnabled) {
            msg("<b>The faction bank system is disabled on this server.");
            return false;
        }

        return true;
    }

    @Override
    public boolean validSenderType(CommandSender sender, boolean informSenderIfNot) {
        boolean superValid = super.validSenderType(sender, informSenderIfNot);
        if (!superValid) {
            return false;
        }

        if (!(this.senderMustBeMember || this.senderMustBeModerator || this.senderMustBeAdmin)) {
            return true;
        }

        if (!(sender instanceof Player)) {
            return false;
        }

        if (!fme.hasFaction()) {
            sender.sendMessage(p.txt.parse("<b>You are not member of any faction."));
            return false;
        }

        if (this.senderMustBeModerator && !fme.getRole().isAtLeast(Role.MODERATOR)) {
            sender.sendMessage(p.txt.parse("<b>Only faction moderators can %s.", this.getHelpShort()));
            return false;
        }

        if (this.senderMustBeAdmin && !fme.getRole().isAtLeast(Role.ADMIN)) {
            sender.sendMessage(p.txt.parse("<b>Only faction admins can %s.", this.getHelpShort()));
            return false;
        }

        return true;
    }

    // -------------------------------------------- //
    // Assertions
    // -------------------------------------------- //

    public boolean assertHasFaction() {
        if (me == null) {
            return true;
        }

        if (!fme.hasFaction()) {
            sendMessage("You are not member of any faction.");
            return false;
        }
        return true;
    }

    public boolean assertMinRole(Role role) {
        if (me == null) {
            return true;
        }

        if (fme.getRole().value < role.value) {
            msg("<b>You <h>must be " + role + "<b> to " + this.getHelpShort() + ".");
            return false;
        }
        return true;
    }

    // -------------------------------------------- //
    // Argument Readers
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
            this.msg("<b>No player \"<p>%s<b>\" could be found.", name);
        }

        return ret;
    }

    public FPlayer argAsFPlayer(int idx, FPlayer def, boolean msg) {
        return this.strAsFPlayer(this.argAsString(idx), def, msg);
    }

    public FPlayer argAsFPlayer(int idx, FPlayer def) {
        return this.argAsFPlayer(idx, def, true);
    }

    public FPlayer argAsFPlayer(int idx) {
        return this.argAsFPlayer(idx, null);
    }

    // BEST FPLAYER MATCH ======================
    public FPlayer strAsBestFPlayerMatch(String name, FPlayer def, boolean msg) {
        return strAsFPlayer(name, def, msg);
    }

    public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def, boolean msg) {
        return this.strAsBestFPlayerMatch(this.argAsString(idx), def, msg);
    }

    public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def) {
        return this.argAsBestFPlayerMatch(idx, def, true);
    }

    public FPlayer argAsBestFPlayerMatch(int idx) {
        return this.argAsBestFPlayerMatch(idx, null);
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
            this.msg("<b>The faction or player \"<p>%s<b>\" could not be found.", name);
        }

        return ret;
    }

    public Faction argAsFaction(int idx, Faction def, boolean msg) {
        return this.strAsFaction(this.argAsString(idx), def, msg);
    }

    public Faction argAsFaction(int idx, Faction def) {
        return this.argAsFaction(idx, def, true);
    }

    public Faction argAsFaction(int idx) {
        return this.argAsFaction(idx, null);
    }

    // -------------------------------------------- //
    // Commonly used logic
    // -------------------------------------------- //

    public boolean canIAdministerYou(FPlayer i, FPlayer you) {
        if (!i.getFaction().equals(you.getFaction())) {
            i.sendMessage(p.txt.parse("%s <b>is not in the same faction as you.", you.describeTo(i, true)));
            return false;
        }

        if (i.getRole().value > you.getRole().value || i.getRole().equals(Role.ADMIN)) {
            return true;
        }

        if (you.getRole().equals(Role.ADMIN)) {
            i.sendMessage(p.txt.parse("<b>Only the faction admin can do that."));
        } else if (i.getRole().equals(Role.MODERATOR)) {
            if (i == you) {
                return true; //Moderators can control themselves
            } else {
                i.sendMessage(p.txt.parse("<b>Moderators can't control each other..."));
            }
        } else {
            i.sendMessage(p.txt.parse("<b>You must be a faction moderator to do that."));
        }

        return false;
    }

    // if economy is enabled and they're not on the bypass list, make 'em pay; returns true unless person can't afford the cost
    public boolean payForCommand(double cost, String toDoThis, String forDoingThis) {
        if (!Econ.shouldBeUsed() || this.fme == null || cost == 0.0 || fme.isAdminBypassing()) {
            return true;
        }

        if (Conf.bankEnabled && Conf.bankFactionPaysCosts && fme.hasFaction()) {
            return Econ.modifyMoney(myFaction, -cost, toDoThis, forDoingThis);
        } else {
            return Econ.modifyMoney(fme, -cost, toDoThis, forDoingThis);
        }
    }

    public boolean payForCommand(double cost, TL toDoThis, TL forDoingThis) {
        return payForCommand(cost, toDoThis.toString(), forDoingThis.toString());
    }

    // like above, but just make sure they can pay; returns true unless person can't afford the cost
    public boolean canAffordCommand(double cost, String toDoThis) {
        if (!Econ.shouldBeUsed() || this.fme == null || cost == 0.0 || fme.isAdminBypassing()) {
            return true;
        }

        if (Conf.bankEnabled && Conf.bankFactionPaysCosts && fme.hasFaction()) {
            return Econ.hasAtLeast(myFaction, cost, toDoThis);
        } else {
            return Econ.hasAtLeast(fme, cost, toDoThis);
        }
    }

    public void doWarmUp(WarmUpUtil.Warmup warmup, TL translationKey, String action, Runnable runnable, long delay) {
        this.doWarmUp(this.fme, warmup, translationKey, action, runnable, delay);
    }

    public void doWarmUp(FPlayer player, WarmUpUtil.Warmup warmup, TL translationKey, String action, Runnable runnable, long delay) {
        WarmUpUtil.process(player, warmup, translationKey, action, runnable, delay);
    }
}
