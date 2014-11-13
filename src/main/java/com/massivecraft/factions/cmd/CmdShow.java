package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

import java.util.Collection;

public class CmdShow extends FCommand {

    public CmdShow() {
        this.aliases.add("show");
        this.aliases.add("who");

        //this.requiredArgs.add("");
        this.optionalArgs.put("faction tag", "yours");

        this.permission = Permission.SHOW.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction faction = myFaction;
        if (this.argIsSet(0)) {
            faction = this.argAsFaction(0);
            if (faction == null) {
                return;
            }
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!payForCommand(Conf.econCostShow, "to show faction information", "for showing faction information")) {
            return;
        }

        Collection<FPlayer> admins = faction.getFPlayersWhereRole(Role.ADMIN);
        Collection<FPlayer> mods = faction.getFPlayersWhereRole(Role.MODERATOR);
        Collection<FPlayer> normals = faction.getFPlayersWhereRole(Role.NORMAL);

        msg(p.txt.titleize(faction.getTag(fme)));
        msg("<a>Description: <i>%s", faction.getDescription());
        if (!faction.isNormal()) {
            return;
        }

        String peaceStatus = "";
        if (faction.isPeaceful()) {
            peaceStatus = "     " + Conf.colorNeutral + "This faction is Peaceful";
        }

        msg("<a>Joining: <i>" + (faction.getOpen() ? "no invitation is needed" : "invitation is required") + peaceStatus);

        double powerBoost = faction.getPowerBoost();
        String boost = (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? " (bonus: " : " (penalty: ") + powerBoost + ")";
        msg("<a>Land / Power / Maxpower: <i> %d/%d/%d %s", faction.getLandRounded(), faction.getPowerRounded(), faction.getPowerMaxRounded(), boost);

        if (faction.isPermanent()) {
            msg("<a>This faction is permanent, remaining even with no members.");
        }

        // show the land value
        if (Econ.shouldBeUsed()) {
            double value = Econ.calculateTotalLandValue(faction.getLandRounded());
            double refund = value * Conf.econClaimRefundMultiplier;
            if (value > 0) {
                String stringValue = Econ.moneyString(value);
                String stringRefund = (refund > 0.0) ? (" (" + Econ.moneyString(refund) + " depreciated)") : "";
                msg("<a>Total land value: <i>" + stringValue + stringRefund);
            }

            //Show bank contents
            if (Conf.bankEnabled) {
                msg("<a>Bank contains: <i>" + Econ.moneyString(Econ.getBalance(faction.getAccountId())));
            }
        }

        FancyMessage allies = new FancyMessage("Allies: ").color(ChatColor.GOLD);
        FancyMessage enemies = new FancyMessage("Enemies: ").color(ChatColor.GOLD);
        for (Faction otherFaction : Factions.getInstance().getAllFactions()) {
            if (otherFaction == faction) {
                continue;
            }

            Relation rel = otherFaction.getRelationTo(faction);
            String s = otherFaction.getTag(fme);
            if (rel.isAlly()) {
                allies.then(s).tooltip(getToolTips(otherFaction));
            } else if (rel.isEnemy()) {
                enemies.then(s).tooltip(getToolTips(otherFaction));
            }
        }


        FancyMessage online = new FancyMessage("Members online: ").color(ChatColor.GOLD);
        FancyMessage offline = new FancyMessage("Members offline: ").color(ChatColor.GOLD);
        for (FPlayer p : faction.getFPlayers()) {
            String name = p.getNameAndTitle();
            if (p.isOnline()) {
                online.then(name).tooltip(getToolTips(p));
            } else {
                offline.then(name).tooltip(getToolTips(p));
            }
        }

        // Send all at once ;D
        sendFancyMessage(allies);
        sendFancyMessage(enemies);
        sendFancyMessage(online);
        sendFancyMessage(offline);
    }

}
