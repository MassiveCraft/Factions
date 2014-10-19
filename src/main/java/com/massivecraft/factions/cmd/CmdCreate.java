package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FactionCreateEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.MiscUtil;

import org.bukkit.Bukkit;

import java.util.ArrayList;


public class CmdCreate extends FCommand {

    public CmdCreate() {
        super();
        this.aliases.add("create");

        this.requiredArgs.add("faction tag");
        //this.optionalArgs.put("", "");

        this.permission = Permission.CREATE.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        String tag = this.argAsString(0);

        if (fme.hasFaction()) {
            msg("<b>You must leave your current faction first.");
            return;
        }

        if (Factions.getInstance().isTagTaken(tag)) {
            msg("<b>That tag is already in use.");
            return;
        }

        ArrayList<String> tagValidationErrors = MiscUtil.validateTag(tag);
        if (tagValidationErrors.size() > 0) {
            sendMessage(tagValidationErrors);
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
        if (!canAffordCommand(Conf.econCostCreate, "to create a new faction")) {
            return;
        }

        // trigger the faction creation event (cancellable)
        FactionCreateEvent createEvent = new FactionCreateEvent(me, tag);
        Bukkit.getServer().getPluginManager().callEvent(createEvent);
        if (createEvent.isCancelled()) {
            return;
        }

        // then make 'em pay (if applicable)
        if (!payForCommand(Conf.econCostCreate, "to create a new faction", "for creating a new faction")) {
            return;
        }

        Faction faction = Factions.getInstance().createFaction();

        // TODO: Why would this even happen??? Auto increment clash??
        if (faction == null) {
            msg("<b>There was an internal error while trying to create your faction. Please try again.");
            return;
        }

        // finish setting up the Faction
        faction.setTag(tag);

        // trigger the faction join event for the creator
        FPlayerJoinEvent joinEvent = new FPlayerJoinEvent(FPlayers.getInstance().getByPlayer(me), faction, FPlayerJoinEvent.PlayerJoinReason.CREATE);
        Bukkit.getServer().getPluginManager().callEvent(joinEvent);
        // join event cannot be cancelled or you'll have an empty faction

        // finish setting up the FPlayer
        fme.setRole(Role.ADMIN);
        fme.setFaction(faction);

        for (FPlayer follower : FPlayers.getInstance().getOnlinePlayers()) {
            follower.msg("%s<i> created a new faction %s", fme.describeTo(follower, true), faction.getTag(follower));
        }

        msg("<i>You should now: %s", p.cmdBase.cmdDescription.getUseageTemplate());

        if (Conf.logFactionCreate) {
            P.p.log(fme.getName() + " created a new faction: " + tag);
        }
    }

}
