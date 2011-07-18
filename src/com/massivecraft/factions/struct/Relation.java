package com.massivecraft.factions.struct;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;


public enum Relation {
	MEMBER(3, "member"),
	ALLY(2, "ally"),
	NEUTRAL(1, "neutral"),
	ENEMY(0, "enemy");
	
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
	
	public boolean isMember() {
		return this == Relation.MEMBER;
	}
	
	public boolean isAlly() {
		return this == Relation.ALLY;
	}
	
	public boolean isNeutral() {
		return this == Relation.NEUTRAL;
	}
	
	public boolean isEnemy() {
		return this == Relation.ENEMY;
	}
	
	public ChatColor getColor() {
		if (this == Relation.MEMBER) {
			return Conf.colorMember;
		} else if (this == Relation.ALLY) {
			return Conf.colorAlly;
		} else if (this == Relation.NEUTRAL) {
			return Conf.colorNeutral;
		} else { //if (relation == FactionRelation.ENEMY) {
			return Conf.colorEnemy;
		}
	}
}
