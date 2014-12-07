package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

import java.util.ArrayList;

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

        ArrayList<FancyMessage> allies = new ArrayList<FancyMessage>();
        ArrayList<FancyMessage> enemies = new ArrayList<FancyMessage>();
        FancyMessage currentAllies = new FancyMessage("Allies: ").color(ChatColor.GOLD);
        FancyMessage currentEnemies = new FancyMessage("Enemies: ").color(ChatColor.GOLD);

        boolean firstAlly = true;
        boolean firstEnemy = true;
        for (Faction otherFaction : Factions.getInstance().getAllFactions()) {
            if (otherFaction == faction) {
                continue;
            }

            Relation rel = otherFaction.getRelationTo(faction);
            String s = otherFaction.getTag(fme);
            if (rel.isAlly()) {
                if (firstAlly) {
                    currentAllies.then(s).tooltip(getToolTips(otherFaction));
                } else {
                    currentAllies.then(", " + s).tooltip(getToolTips(otherFaction));
                }
                firstAlly = false;

                if (currentAllies.toJSONString().length() > Short.MAX_VALUE) {
                    allies.add(currentAllies);
                    currentAllies = new FancyMessage();
                }
            } else if (rel.isEnemy()) {
                if (firstEnemy) {
                    currentEnemies.then(s).tooltip(getToolTips(otherFaction));
                } else {
                    currentEnemies.then(", " + s).tooltip(getToolTips(otherFaction));
                }
                firstEnemy = false;

                if (currentEnemies.toJSONString().length() > Short.MAX_VALUE) {
                    enemies.add(currentEnemies);
                    currentEnemies = new FancyMessage();
                }
            }
        }
        allies.add(currentAllies);
        enemies.add(currentEnemies);

        FancyMessage online = new FancyMessage("Members online: ").color(ChatColor.GOLD);
        FancyMessage offline = new FancyMessage("Members offline: ").color(ChatColor.GOLD);
        boolean firstOnline = true;
        boolean firstOffline = true;
        for (FPlayer p : faction.getFPlayers()) {
            String name = p.getNameAndTitle();
            if (p.isOnline()) {
                if (firstOnline) {
                    online.then(name).tooltip(getToolTips(p));
                } else {
                    online.then(", " + name).tooltip(getToolTips(p));
                }
                firstOnline = false;
                if (online.toJSONString().length() > Short.MAX_VALUE) {
                    online = new FancyMessage();
                }
            } else {
                if (firstOffline) {
                    offline.then(name).tooltip(getToolTips(p));
                } else {
                    offline.then(", " + name).tooltip(getToolTips(p));
                }
                firstOffline = false;
                if (offline.toJSONString().length() > Short.MAX_VALUE) {
                    offline = new FancyMessage();
                }
            }
        }

        // Send all at once ;D
        sendFancyMessage(allies);
        sendFancyMessage(enemies);
        sendFancyMessage(online);
        sendFancyMessage(offline);
    }

}
