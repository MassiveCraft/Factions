package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;

public class CmdPeaceful extends FCommand {

    public CmdPeaceful() {
        super();
        this.aliases.add("peaceful");

        this.requiredArgs.add("faction tag");
        //this.optionalArgs.put("", "");

        this.permission = Permission.SET_PEACEFUL.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction faction = this.argAsFaction(0);
        if (faction == null) return;

        String change;
        if (faction.isPeaceful()) {
            change = "removed peaceful status from";
            faction.setPeaceful(false);
        } else {
            change = "granted peaceful status to";
            faction.setPeaceful(true);
        }

        // Inform all players
        for (FPlayer fplayer : FPlayers.i.getOnline()) {
            if (fplayer.getFaction() == faction) {
                fplayer.msg((fme == null ? "A server admin" : fme.describeTo(fplayer, true)) + "<i> has " + change + " your faction.");
            } else {
                fplayer.msg((fme == null ? "A server admin" : fme.describeTo(fplayer, true)) + "<i> has " + change + " the faction \"" + faction.getTag(fplayer) + "<i>\".");
            }
        }

    }

}
