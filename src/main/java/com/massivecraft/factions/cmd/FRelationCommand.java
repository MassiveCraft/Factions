package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FactionRelationEvent;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public abstract class FRelationCommand extends FCommand {

    public Relation targetRelation;

    public FRelationCommand() {
        super();
        this.requiredArgs.add("faction tag");
        //this.optionalArgs.put("player name", "you");

        this.permission = Permission.RELATION.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = true;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction them = this.argAsFaction(0);
        if (them == null) {
            return;
        }

        if (!them.isNormal()) {
            msg("<b>Nope! You can't.");
            return;
        }

        if (them == myFaction) {
            msg("<b>Nope! You can't declare a relation to yourself :)");
            return;
        }

        if (myFaction.getRelationWish(them) == targetRelation) {
            msg("<b>You already have that relation wish set with %s.", them.getTag());
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!payForCommand(targetRelation.getRelationCost(), "to change a relation wish", "for changing a relation wish")) {
            return;
        }

        // try to set the new relation
        Relation oldRelation = myFaction.getRelationTo(them, true);
        myFaction.setRelationWish(them, targetRelation);
        Relation currentRelation = myFaction.getRelationTo(them, true);
        ChatColor currentRelationColor = currentRelation.getColor();

        // if the relation change was successful
        if (targetRelation.value == currentRelation.value) {
            // trigger the faction relation event
            FactionRelationEvent relationEvent = new FactionRelationEvent(myFaction, them, oldRelation, currentRelation);
            Bukkit.getServer().getPluginManager().callEvent(relationEvent);

            them.msg("<i>Your faction is now " + currentRelationColor + targetRelation.toString() + "<i> to " + currentRelationColor + myFaction.getTag());
            myFaction.msg("<i>Your faction is now " + currentRelationColor + targetRelation.toString() + "<i> to " + currentRelationColor + them.getTag());
        }
        // inform the other faction of your request
        else {
            them.msg(currentRelationColor + myFaction.getTag() + "<i> wishes to be your " + targetRelation.getColor() + targetRelation.toString());
            them.msg("<i>Type <c>/" + Conf.baseCommandAliases.get(0) + " " + targetRelation + " " + myFaction.getTag() + "<i> to accept.");
            myFaction.msg(currentRelationColor + them.getTag() + "<i> were informed that you wish to be " + targetRelation.getColor() + targetRelation);
        }

        if (!targetRelation.isNeutral() && them.isPeaceful()) {
            them.msg("<i>This will have no effect while your faction is peaceful.");
            myFaction.msg("<i>This will have no effect while their faction is peaceful.");
        }

        if (!targetRelation.isNeutral() && myFaction.isPeaceful()) {
            them.msg("<i>This will have no effect while their faction is peaceful.");
            myFaction.msg("<i>This will have no effect while your faction is peaceful.");
        }

        FTeamWrapper.updatePrefixes(myFaction);
        FTeamWrapper.updatePrefixes(them);
    }
}
