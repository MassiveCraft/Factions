package org.mcteam.factions.commands;

import org.mcteam.factions.struct.Relation;

public class FCommandRelationNeutral extends FRelationCommand {
	
	public FCommandRelationNeutral() {
		aliases.add("neutral");
	}
	
	public void perform() {
		
		if( isLocked() ) {
			sendLockMessage();
			return;
		}
		
		relation(Relation.NEUTRAL, parameters.get(0));
	}
	
}
