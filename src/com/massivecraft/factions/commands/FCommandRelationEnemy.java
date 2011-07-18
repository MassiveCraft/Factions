package com.massivecraft.factions.commands;

import com.massivecraft.factions.struct.Relation;

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
