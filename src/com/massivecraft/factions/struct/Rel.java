package com.massivecraft.factions.struct;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;

public enum Rel
{
	LEADER   (70, "leader"),
	OFFICER  (60, "officer"),
	MEMBER   (50, "member"),
	ALLY     (40, "ally"),
	TRUCE    (30, "truce"),
	NEUTRAL  (20, "neutral"),
	ENEMY    (10, "enemy"),
	;
	
	public final int value;
	public final String nicename;
	
	private Rel(final int value, final String nicename)
	{
		this.value = value;
		this.nicename = nicename;
	}
	
	public static Rel parse(String str)
	{
		if (str == null || str.length() < 1) return null;
		
		str = str.toLowerCase();
		
		// These are to allow conversion from the old system.
		if (str.equals("admin"))
		{
			return LEADER;
		}
		
		if (str.equals("moderator"))
		{
			return OFFICER;
		}
		
		if (str.equals("normal"))
		{
			return MEMBER;
		}
		
		// This is how we check: Based on first char.
		char c = str.charAt(0);
		if (c == 'l') return LEADER;
		if (c == 'o') return OFFICER;
		if (c == 'm') return MEMBER;
		if (c == 'a') return ALLY;
		if (c == 't') return TRUCE;
		if (c == 'n') return NEUTRAL;
		if (c == 'e') return ENEMY;
		return null;
	}
	
	@Override
	public String toString()
	{
		return this.nicename;
	}
	
	public boolean isAtLeast(Rel rel)
	{
		return this.value >= rel.value;
	}
	
	public boolean isAtMost(Rel rel)
	{
		return this.value <= rel.value;
	}
	
	public boolean isLessThan(Rel rel)
	{
		return this.value < rel.value;
	}
	
	public ChatColor getColor()
	{
		if (this == MEMBER)
			return Conf.colorMember;
		else if (this == ALLY)
			return Conf.colorAlly;
		else if (this == NEUTRAL)
			return Conf.colorNeutral;
		else if (this == TRUCE)
			return Conf.colorTruce;
		else
			return Conf.colorEnemy;
	}
	
	public String getPrefix()
	{
		if (this == LEADER)
		{
			return Conf.prefixLeader;
		} 
		
		if (this == OFFICER)
		{
			return Conf.prefixOfficer;
		}
		
		return "";
	}
	
	// TODO: ADD TRUCE!!!!
	// TODO.... or remove it...
	public double getRelationCost()
	{
		if (this == ENEMY)
			return Conf.econCostEnemy;
		else if (this == ALLY)
			return Conf.econCostAlly;
		else
			return Conf.econCostNeutral;
	}
	
	// return appropriate Conf setting for DenyBuild based on this relation and their online status
	public boolean confDenyBuild(boolean online)
	{
		if (this == MEMBER)
			return false;

		if (online)
		{
			if (this == ENEMY)
				return Conf.territoryEnemyDenyBuild;
			else if (this == ALLY)
				return Conf.territoryAllyDenyBuild;
			else if (this == TRUCE)
				return Conf.territoryTruceDenyBuild;
			else
				return Conf.territoryDenyBuild;
		}
		else
		{
			if (this == ENEMY)
				return Conf.territoryEnemyDenyBuildWhenOffline;
			else if (this == ALLY)
				return Conf.territoryAllyDenyBuildWhenOffline;
			else if (this == TRUCE)
				return Conf.territoryTruceDenyBuildWhenOffline;
			else
				return Conf.territoryDenyBuildWhenOffline;
		}
	}

	// return appropriate Conf setting for PainBuild based on this relation and their online status
	public boolean confPainBuild(boolean online)
	{
		if (this == MEMBER)
			return false;

		if (online)
		{
			if (this == ENEMY)
				return Conf.territoryEnemyPainBuild;
			else if (this == ALLY)
				return Conf.territoryAllyPainBuild;
			else if (this == TRUCE)
				return Conf.territoryTrucePainBuild;
			else
				return Conf.territoryPainBuild;
		}
		else
		{
			if (this == ENEMY) 
				return Conf.territoryEnemyPainBuildWhenOffline;
			else if (this == ALLY)
				return Conf.territoryAllyPainBuildWhenOffline;
			else if (this == TRUCE)
				return Conf.territoryTrucePainBuildWhenOffline;
			else
				return Conf.territoryPainBuildWhenOffline;
		}
	}

	// return appropriate Conf setting for DenyUseage based on this relation
	public boolean confDenyUseage()
	{
		if (this == MEMBER)
			return false;
		else if (this == ENEMY)
			return Conf.territoryEnemyDenyUseage;
		else if (this == ALLY)
			return Conf.territoryAllyDenyUseage;
		else if (this == TRUCE)
			return Conf.territoryTruceDenyUseage;
		else
			return Conf.territoryDenyUseage;
	}
}
