package org.mcteam.factions.commands;

import org.mcteam.factions.struct.Relation;

public class FCommandRelationEnemy extends FRelationCommand {
	
	public FCommandRelationEnemy() {
		aliases.add("enemy");
	}
	
	@Override
	public void perform() {
		
		if( isLocked() ) {
			sendLockMessage();
			return;
		}
		
		relation(Relation.ENEMY, parameters.get(0));
	}
	
}
