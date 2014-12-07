package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.zcore.util.TL;

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
        if (!payForCommand(Conf.econCostShow, TL.COMMAND_SHOW_TOSHOW, TL.COMMAND_SHOW_FORSHOW)) {
            return;
        }

        msg(p.txt.titleize(faction.getTag(fme)));
        msg(TL.COMMAND_SHOW_DESCRIPTION, faction.getDescription());
        if (!faction.isNormal()) {
            return;
        }

        String peaceStatus = "";
        if (faction.isPeaceful()) {
            peaceStatus = "     " + Conf.colorNeutral + TL.COMMAND_SHOW_PEACEFUL.toString();
        }

        msg(TL.COMMAND_SHOW_JOINING.toString() + peaceStatus,(faction.getOpen() ? TL.COMMAND_SHOW_UNINVITED.toString() : TL.COMMAND_SHOW_INVITATION.toString()));

        double powerBoost = faction.getPowerBoost();
        String boost = (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? TL.COMMAND_SHOW_BONUS.toString():TL.COMMAND_SHOW_PENALTY.toString() + powerBoost + ")");
        msg(TL.COMMAND_SHOW_POWER, faction.getLandRounded(), faction.getPowerRounded(), faction.getPowerMaxRounded(), boost);

        if (faction.isPermanent()) {
            msg(TL.COMMAND_SHOW_PERMANENT);
        }

        // show the land value
        if (Econ.shouldBeUsed()) {
            double value = Econ.calculateTotalLandValue(faction.getLandRounded());
            double refund = value * Conf.econClaimRefundMultiplier;
            if (value > 0) {
                String stringValue = Econ.moneyString(value);
                String stringRefund = (refund > 0.0) ? (TL.COMMAND_SHOW_DEPRECIATED.format(Econ.moneyString(refund))) : "";
                msg(TL.COMMAND_SHOW_LANDVALUE,stringValue,stringRefund);
            }

            //Show bank contents
            if (Conf.bankEnabled) {
                msg(TL.COMMAND_SHOW_BANKCONTAINS,Econ.moneyString(Econ.getBalance(faction.getAccountId())));
            }
        }

        ArrayList<FancyMessage> allies = new ArrayList<FancyMessage>();
        ArrayList<FancyMessage> enemies = new ArrayList<FancyMessage>();
        FancyMessage currentAllies = new FancyMessage(TL.COMMAND_SHOW_ALLIES.toString()).color(ChatColor.GOLD);
        FancyMessage currentEnemies = new FancyMessage(TL.COMMAND_SHOW_ENEMIES.toString()).color(ChatColor.GOLD);

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

        FancyMessage online = new FancyMessage(TL.COMMAND_SHOW_MEMBERSONLINE.toString()).color(ChatColor.GOLD);
        FancyMessage offline = new FancyMessage(TL.COMMAND_SHOW_MEMBERSOFFLINE.toString()).color(ChatColor.GOLD);
        boolean firstOnline = true;
        boolean firstOffline = true;
        for (FPlayer p : MiscUtil.rankOrder(faction.getFPlayers())) {
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
