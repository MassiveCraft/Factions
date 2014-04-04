package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;

public class CmdPermanentPower extends FCommand {
    public CmdPermanentPower() {
        super();
        this.aliases.add("permanentpower");

        this.requiredArgs.add("faction");
        this.optionalArgs.put("power", "reset");

        this.permission = Permission.SET_PERMANENTPOWER.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction targetFaction = this.argAsFaction(0);
        if (targetFaction == null) return;

        Integer targetPower = this.argAsInt(1);

        targetFaction.setPermanentPower(targetPower);

        String change = "removed permanentpower status from";
        if (targetFaction.hasPermanentPower()) {
            change = "added permanentpower status to";
        }

        msg("<i>You %s <h>%s<i>.", change, targetFaction.describeTo(fme));

        // Inform all players
        for (FPlayer fplayer : targetFaction.getFPlayersWhereOnline(true)) {
            fplayer.msg((fme == null ? "A server admin" : fme.describeTo(fplayer, true)) + "<i> " + change + " your faction.");
        }
    }
}
