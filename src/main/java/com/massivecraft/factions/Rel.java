package com.massivecraft.factions;

import org.bukkit.ChatColor;

import com.massivecraft.factions.entity.MConf;


public enum Rel
{
	// -------------------------------------------- //
	// ENUM
	// -------------------------------------------- //
	
	LEADER (70, true, "your faction leader", "your faction leader", "", ""),
	OFFICER (60, true, "an officer in your faction", "officers in your faction", "", ""),
	MEMBER (50, true, "a member in your faction", "members in your faction", "your faction", "your factions"),
	RECRUIT (45, true, "a recruit in your faction", "recruits in your faction", "", ""),
	ALLY (40, true, "an ally", "allies", "an allied faction", "allied factions"),
	TRUCE (30, true, "someone in truce with you", "those in truce with you", "a faction in truce", "factions in truce"),
	NEUTRAL (20, false, "someone neutral to you", "those neutral to you", "a neutral faction", "neutral factions"),
	ENEMY (10, false, "an enemy", "enemies", "an enemy faction", "enemy factions"),
	
	// END OF LIST
	;
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	// TODO: Are not enums sorted without this?
	private final int value;
	public int getValue() { return this.value; }
	
	// Used for friendly fire.
	private final boolean friend;
	public boolean isFriend() { return this.friend; }
	
	private final String descPlayerOne;
	public String getDescPlayerOne() { return this.descPlayerOne; }
	
	private final String descPlayerMany;
	public String getDescPlayerMany() { return this.descPlayerMany; }
	
	private final String descFactionOne;
	public String getDescFactionOne() { return this.descFactionOne; }
	
	private final String descFactionMany;
	public String getDescFactionMany() { return this.descFactionMany; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	private Rel(final int value, final boolean friend, final String descPlayerOne, final String descPlayerMany, final String descFactionOne, final String descFactionMany)
	{
		this.value = value;
		this.friend = friend;
		this.descPlayerOne = descPlayerOne;
		this.descPlayerMany = descPlayerMany;
		this.descFactionOne = descFactionOne;
		this.descFactionMany = descFactionMany;
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
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
			return MConf.get().colorMember;
		else if (this == ALLY)
			return MConf.get().colorAlly;
		else if (this == NEUTRAL)
			return MConf.get().colorNeutral;
		else if (this == TRUCE)
			return MConf.get().colorTruce;
		else
			return MConf.get().colorEnemy;
	}
	
	public String getPrefix()
	{
		if (this == LEADER)
		{
			return MConf.get().prefixLeader;
		} 
		
		if (this == OFFICER)
		{
			return MConf.get().prefixOfficer;
		}
		
		if (this == MEMBER)
		{
			return MConf.get().prefixMember;
		}
		
		if (this == RECRUIT)
		{
			return MConf.get().prefixRecruit;
		}
		
		return "";
	}
	
}
