package com.massivecraft.factions.commands;

import com.massivecraft.factions.struct.Relation;

public class FCommandRelationAlly extends FRelationCommand {
	
	public FCommandRelationAlly() {
		aliases.add("ally");
	}
	
	@Override
	public void perform() {
		
		if( isLocked() ) {
			sendLockMessage();
			return;
		}
		
		relation(Relation.ALLY, parameters.get(0));
	}
	
}
