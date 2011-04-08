package org.mcteam.factions.commands;

import org.mcteam.factions.struct.Relation;

public class FCommandRelationAlly extends FRelationCommand {
	
	public FCommandRelationAlly() {
		aliases.add("ally");
	}
	
	public void perform() {
		relation(Relation.ALLY, parameters.get(0));
	}
	
}
