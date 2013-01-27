package com.massivecraft.factions.struct;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;

public enum Rel
{
	LEADER   (70, "your faction leader", "your faction leader", "", ""),
	OFFICER  (60, "an officer in your faction", "officers in your faction", "", ""),
	MEMBER   (50, "a member in your faction", "members in your faction", "your faction", "your factions"),
	RECRUIT  (45, "a recruit in your faction", "recruits in your faction", "", ""),
	ALLY     (40, "an ally", "allies", "an allied faction", "allied factions"),
	TRUCE    (30, "someone in truce with you", "those in truce with you", "a faction in truce", "factions in truce"),
	NEUTRAL  (20, "someone neutral to you", "those neutral to you", "a neutral faction", "neutral factions"),
	ENEMY    (10, "an enemy", "enemies", "an enemy faction", "enemy factions"),
	;
	
	private final int value;
	private final String descPlayerOne;
	public String getDescPlayerOne() { return this.descPlayerOne; }
	
	private final String descPlayerMany;
	public String getDescPlayerMany() { return this.descPlayerMany; }
	
	private final String descFactionOne;
	public String getDescFactionOne() { return this.descFactionOne; }
	
	private final String descFactionMany;
	public String getDescFactionMany() { return this.descFactionMany; }
	
	private Rel(final int value, final String descPlayerOne, final String descPlayerMany, final String descFactionOne, final String descFactionMany)
	{
		this.value = value;
		this.descPlayerOne = descPlayerOne;
		this.descPlayerMany = descPlayerMany;
		this.descFactionOne = descFactionOne;
		this.descFactionMany = descFactionMany;
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
		if (c == 'r') return RECRUIT;
		if (c == 'a') return ALLY;
		if (c == 't') return TRUCE;
		if (c == 'n') return NEUTRAL;
		if (c == 'e') return ENEMY;
		return null;
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
	
	public boolean isMoreThan(Rel rel)
	{
		return this.value > rel.value;
	}
	
	public ChatColor getColor()
	{
		if (this.isAtLeast(RECRUIT))
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
		
		if (this == MEMBER)
		{
			return Conf.prefixMember;
		}
		
		if (this == RECRUIT)
		{
			return Conf.prefixRecruit;
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
		else if (this == TRUCE)
			return Conf.econCostTruce;
		else
			return Conf.econCostNeutral;
	}
}
