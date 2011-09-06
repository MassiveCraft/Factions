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
		return this.value == MEMBER.value;
	}
	
	public boolean isAlly() {
		return this.value == ALLY.value;
	}
	
	public boolean isNeutral() {
		return this.value == NEUTRAL.value;
	}
	
	public boolean isEnemy() {
		return this.value == ENEMY.value;
	}
	
	public boolean isAtLeast(Relation relation) {
		return this.value >= relation.value;
	}
	
	public boolean isAtMost(Relation relation) {
		return this.value <= relation.value;
	}
	
	public ChatColor getColor() {
		if (this.value == MEMBER.value) {
			return Conf.colorMember;
		} else if (this.value == ALLY.value) {
			return Conf.colorAlly;
		} else if (this.value == NEUTRAL.value) {
			return Conf.colorNeutral;
		} else {
			return Conf.colorEnemy;
		}
	}

	// return appropriate Conf setting for DenyBuild based on this relation and their online status
	public boolean confDenyBuild(boolean online) {
		if (online) {
			if (isEnemy()) {
				return Conf.territoryEnemyDenyBuild;
			}
			else if (isAlly()) {
				return Conf.territoryAllyDenyBuild;
			}
			else {
				return Conf.territoryDenyBuild;
			}
		}
		else {
			if (isEnemy()) {
				return Conf.territoryEnemyDenyBuildWhenOffline;
			}
			else if (isAlly()) {
				return Conf.territoryAllyDenyBuildWhenOffline;
			}
			else {
				return Conf.territoryDenyBuildWhenOffline;
			}
		}
	}

	// return appropriate Conf setting for PainBuild based on this relation and their online status
	public boolean confPainBuild(boolean online) {
		if (online) {
			if (isEnemy()) {
				return Conf.territoryEnemyPainBuild;
			}
			else if (isAlly()) {
				return Conf.territoryAllyPainBuild;
			}
			else {
				return Conf.territoryPainBuild;
			}
		}
		else {
			if (isEnemy()) {
				return Conf.territoryEnemyPainBuildWhenOffline;
			}
			else if (isAlly()) {
				return Conf.territoryAllyPainBuildWhenOffline;
			}
			else {
				return Conf.territoryPainBuildWhenOffline;
			}
		}
	}

	// return appropriate Conf setting for DenyUseage based on this relation
	public boolean confDenyUseage() {
		if (isEnemy()) {
			return Conf.territoryEnemyDenyUseage;
		}
		else if (isAlly()) {
			return Conf.territoryAllyDenyUseage;
		}
		else {
			return Conf.territoryDenyUseage;
		}
	}
}
