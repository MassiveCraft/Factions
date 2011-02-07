package com.bukkit.mcteam.factions.struct;

import org.bukkit.ChatColor;

import com.bukkit.mcteam.factions.entities.*;

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
	
	public ChatColor getColor() {
		return Conf.relationColor(this);
	}
}
