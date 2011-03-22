package com.bukkit.mcteam.factions.commands;

import com.bukkit.mcteam.factions.struct.Relation;

public class FCommandRelationAlly extends FRelationCommand {
	
	public void perform() {
		relation(Relation.ALLY, parameters.get(0));
	}
	
}
