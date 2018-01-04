package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Relation;

public class CmdRelationTruce extends FRelationCommand {

    public CmdRelationTruce() {
        aliases.add("truce");
        targetRelation = Relation.TRUCE;
    }
}
