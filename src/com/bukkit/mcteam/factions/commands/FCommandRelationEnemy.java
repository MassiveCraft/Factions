package com.bukkit.mcteam.factions.commands;

import com.bukkit.mcteam.factions.struct.Relation;

public class FCommandRelationEnemy extends FRelationCommand {
	
	public void perform() {
		relation(Relation.ENEMY, parameters.get(0));
	}
	
}
