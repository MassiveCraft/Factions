package org.mcteam.factions.commands;

import org.mcteam.factions.struct.Relation;

public class FCommandRelationEnemy extends FRelationCommand {
	
	public FCommandRelationEnemy() {
		aliases.add("enemy");
	}
	
	public void perform() {
		relation(Relation.ENEMY, parameters.get(0));
	}
	
}
