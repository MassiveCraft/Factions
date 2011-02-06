package com.bukkit.mcteam.factions.struct;

import org.bukkit.ChatColor;

import com.bukkit.mcteam.factions.entities.*;

public enum Relation {
	MEMBER(3, "member"),
	ALLY(2, "ally"),
	NEUTRAL(1, "neutral"),
	ENEMY(0, "enemy");
	//UNKNOWN(-1, "unknown");
	
	public final int value;
	public final String nicename;
	
	private Relation(final int value, final String nicename) {
        this.value = value;
        this.nicename = nicename;
    }
	
	@Override
	public String toString() {
		return this.nicename;
	}
	
	public ChatColor getColor() {
		return Conf.relationColor(this);
	}
	
	/*public String getChartDot() {
		return Conf.chartDot(this);
	}
	
	public static Relation from(String str) {
		if (str.equalsIgnoreCase("member")) {
			return Relation.MEMBER;
		} else if (str.equalsIgnoreCase("ally")) {
			return Relation.ALLY;
		} else if (str.equalsIgnoreCase("neutral")) {
			return Relation.NEUTRAL;
		} else if (str.equalsIgnoreCase("enemy")) {
			return Relation.ENEMY;
		}
		
		return Relation.UNKNOWN;
	}*/
}
