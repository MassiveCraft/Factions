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

        this.requiredArgs.add("p/f/player/faction");
        this.requiredArgs.add("name");
        this.requiredArgs.add("#/reset");

        this.requirements = new CommandRequirements.Builder(Permission.POWERBOOST).build();
    }

    @Override
    public void perform(CommandContext context) {
        String type = context.argAsString(0).toLowerCase();
        boolean doPlayer = true;
        if (type.equals("f") || type.equals("faction")) {
            doPlayer = false;
        } else if (!type.equals("p") && !type.equals("player")) {
            context.msg(TL.COMMAND_POWERBOOST_HELP_1);
            context.msg(TL.COMMAND_POWERBOOST_HELP_2);
            return;
        }

        Double targetPower = context.argAsDouble(2);
        if (targetPower == null) {
            if (context.argAsString(2).equalsIgnoreCase("reset")) {
                targetPower = 0D;
            } else {
                context.msg(TL.COMMAND_POWERBOOST_INVALIDNUM);
                return;
            }
        }

        String target;

        if (doPlayer) {
            FPlayer targetPlayer = context.argAsBestFPlayerMatch(1);
            if (targetPlayer == null) {
                return;
            }

            if (targetPower != 0) {
                targetPower += targetPlayer.getPowerBoost();
            }
            targetPlayer.setPowerBoost(targetPower);
            target = TL.COMMAND_POWERBOOST_PLAYER.format(targetPlayer.getName());
        } else {
            Faction targetFaction = context.argAsFaction(1);
            if (targetFaction == null) {
                return;
            }

            if (targetPower != 0) {
                targetPower += targetFaction.getPowerBoost();
            }
            targetFaction.setPowerBoost(targetPower);
            target = TL.COMMAND_POWERBOOST_FACTION.format(targetFaction.getTag());
        }

        int roundedPower = (int) Math.round(targetPower);
        context.msg(TL.COMMAND_POWERBOOST_BOOST, target, roundedPower);
        if (context.player != null) {
            P.p.log(TL.COMMAND_POWERBOOST_BOOSTLOG.toString(), context.fPlayer.getName(), target, roundedPower);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_POWERBOOST_DESCRIPTION;
    }
}
