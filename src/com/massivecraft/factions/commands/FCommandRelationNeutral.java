package com.massivecraft.factions.commands;

import com.massivecraft.factions.struct.Relation;

public class FCommandRelationNeutral extends FRelationCommand {
	
	public FCommandRelationNeutral() {
		aliases.add("neutral");
	}
	
	@Override
	public void perform() {
		
		if( isLocked() ) {
			sendLockMessage();
			return;
		}
		
		relation(Relation.NEUTRAL, parameters.get(0));
	}
	
}
