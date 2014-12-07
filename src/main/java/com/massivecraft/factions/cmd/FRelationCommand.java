package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FactionRelationEvent;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TL;

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
            msg(TL.COMMAND_RELATIONS_ALLTHENOPE);
            return;
        }

        if (them == myFaction) {
            msg(TL.COMMAND_RELATIONS_MORENOPE);
            return;
        }

        if (myFaction.getRelationWish(them) == targetRelation) {
            msg(TL.COMMAND_RELATIONS_ALREADYINRELATIONSHIP, them.getTag());
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!payForCommand(targetRelation.getRelationCost(),TL.COMMAND_RELATIONS_TOMARRY , TL.COMMAND_RELATIONS_FORMARRY)) {
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

            them.msg(TL.COMMAND_RELATIONS_MUTUAL, currentRelationColor + targetRelation.getTranslation() ,currentRelationColor + myFaction.getTag());
            myFaction.msg(TL.COMMAND_RELATIONS_MUTUAL, currentRelationColor + targetRelation.getTranslation() ,currentRelationColor + myFaction.getTag());
        }
        // inform the other faction of your request
        else {
        	
            them.msg(TL.COMMAND_RELATIONS_PROPOSAL_1,currentRelationColor + myFaction.getTag(),targetRelation.getColor() + targetRelation.getTranslation());
            them.msg(TL.COMMAND_RELATIONS_PROPOSAL_2,Conf.baseCommandAliases.get(0),targetRelation,myFaction.getTag());
            myFaction.msg(TL.COMMAND_RELATIONS_PROPOSAL_SENT,currentRelationColor + them.getTag(),"" + targetRelation.getColor() + targetRelation);
        }

        if (!targetRelation.isNeutral() && them.isPeaceful()) {
            them.msg(TL.COMMAND_RELATIONS_PEACEFUL);
            myFaction.msg(TL.COMMAND_RELATIONS_PEACEFULOTHER);
        }

        if (!targetRelation.isNeutral() && myFaction.isPeaceful()) {
            them.msg(TL.COMMAND_RELATIONS_PEACEFULOTHER);
            myFaction.msg(TL.COMMAND_RELATIONS_PEACEFUL);
        }

        FTeamWrapper.updatePrefixes(myFaction);
        FTeamWrapper.updatePrefixes(them);
    }
}
