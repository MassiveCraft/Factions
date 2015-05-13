package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPowerBoost extends FCommand {

    public CmdPowerBoost() {
        super();
        this.aliases.add("powerboost");

        this.requiredArgs.add("p|f|player|faction");
        this.requiredArgs.add("name");
        this.requiredArgs.add("#");

        this.permission = Permission.POWERBOOST.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        String type = this.argAsString(0).toLowerCase();
        boolean doPlayer = true;
        if (type.equals("f") || type.equals("faction")) {
            doPlayer = false;
        } else if (!type.equals("p") && !type.equals("player")) {
            msg(TL.COMMAND_POWERBOOST_HELP_1);
            msg(TL.COMMAND_POWERBOOST_HELP_2);
            return;
        }

        Double targetPower = this.argAsDouble(2);
        if (targetPower == null) {
            msg(TL.COMMAND_POWERBOOST_INVALIDNUM);
            return;
        }

        String target;

        if (doPlayer) {
            FPlayer targetPlayer = this.argAsBestFPlayerMatch(1);
            if (targetPlayer == null) {
                return;
            }
            targetPlayer.setPowerBoost(targetPower);
            target = TL.COMMAND_POWERBOOST_PLAYER.format(targetPlayer.getName());
        } else {
            Faction targetFaction = this.argAsFaction(1);
            if (targetFaction == null) {
                return;
            }
            targetFaction.setPowerBoost(targetPower);
            target = TL.COMMAND_POWERBOOST_FACTION.format(targetFaction.getTag());
        }

        int roundedPower = (int) Math.round(targetPower);
        msg(TL.COMMAND_POWERBOOST_BOOST, target, roundedPower);
        if (!senderIsConsole) {
            P.p.log(TL.COMMAND_POWERBOOST_BOOSTLOG.toString(), fme.getName(), target, roundedPower);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_POWERBOOST_DESCRIPTION;
    }
}
