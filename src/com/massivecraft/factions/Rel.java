package com.massivecraft.factions;

import java.util.Collections;
import java.util.Set;

import org.bukkit.ChatColor;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.collections.MassiveSet;


public enum Rel
{
	// -------------------------------------------- //
	// ENUM
	// -------------------------------------------- //
	
	ENEMY(
		"an enemy", "enemies", "an enemy faction", "enemy factions",
		"Enemy"
	),
	NEUTRAL(
		"someone neutral to you", "those neutral to you", "a neutral faction", "neutral factions",
		"Neutral"
	),
	TRUCE(
		"someone in truce with you", "those in truce with you", "a faction in truce", "factions in truce",
		"Truce"
	),
	ALLY(
		"an ally", "allies", "an allied faction", "allied factions",
		"Ally"
	),
	RECRUIT(
		"a recruit in your faction", "recruits in your faction", "", "",
		"Recruit"
	),
	MEMBER(
		"a member in your faction", "members in your faction", "your faction", "your factions",
		"Member"
	),
	OFFICER
	(
		"an officer in your faction", "officers in your faction", "", "",
		"Officer", "Moderator"
	),
	COLEADER
	(
		"a coleader in your faction", "coleaders in your faction", "", "",
		"Coleader"
	),
	LEADER("your faction leader", "your faction leader", "", "",
		"Leader", "Admin", "Owner"
	),
	
	// END OF LIST
	;
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public int getValue() { return this.ordinal(); }
	
	private final String descPlayerOne;
	public String getDescPlayerOne() { return this.descPlayerOne; }
	
	private final String descPlayerMany;
	public String getDescPlayerMany() { return this.descPlayerMany; }
	
	private final String descFactionOne;
	public String getDescFactionOne() { return this.descFactionOne; }
	
	private final String descFactionMany;
	public String getDescFactionMany() { return this.descFactionMany; }
	
	private final Set<String> names;
	public Set<String> getNames() { return this.names; }
	public String getName() { return this.getNames().iterator().next(); }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	private Rel(String descPlayerOne, String descPlayerMany, String descFactionOne, String descFactionMany, String... names)
	{
		this.descPlayerOne = descPlayerOne;
		this.descPlayerMany = descPlayerMany;
		this.descFactionOne = descFactionOne;
		this.descFactionMany = descFactionMany;
		this.names = Collections.unmodifiableSet(new MassiveSet<String>(names));
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public boolean isAtLeast(Rel rel)
	{
		return this.getValue() >= rel.getValue();
	}
	
	public boolean isAtMost(Rel rel)
	{
		return this.getValue() <= rel.getValue();
	}
	
	public boolean isLessThan(Rel rel)
	{
		return this.getValue() < rel.getValue();
	}
	
	public boolean isMoreThan(Rel rel)
	{
		return this.getValue() > rel.getValue();
	}
	
	public boolean isRank()
	{
		return this.isAtLeast(Rel.RECRUIT);
	}
	
	// Used for friendly fire.
	public boolean isFriend()
	{
		return this.isAtLeast(TRUCE);
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
		
		if (this == COLEADER)
		{
			return MConf.get().prefixCoLeader;
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
