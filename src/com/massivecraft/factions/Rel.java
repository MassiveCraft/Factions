package com.massivecraft.factions;

import java.util.Collections;
import java.util.Set;

import org.bukkit.ChatColor;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.Colorized;
import com.massivecraft.massivecore.Named;
import com.massivecraft.massivecore.collections.MassiveSet;

public enum Rel implements Colorized, Named
{
	// -------------------------------------------- //
	// ENUM
	// -------------------------------------------- //
	
	ENEMY(
		"an enemy", "enemies", "an enemy faction", "enemy factions",
		"Enemy"
	) { @Override public ChatColor getColor() { return MConf.get().colorEnemy; } },
	
	NEUTRAL(
		"someone neutral to you", "those neutral to you", "a neutral faction", "neutral factions",
		"Neutral"
	) { @Override public ChatColor getColor() { return MConf.get().colorNeutral; } },
	
	TRUCE(
		"someone in truce with you", "those in truce with you", "a faction in truce", "factions in truce",
		"Truce"
	) { @Override public ChatColor getColor() { return MConf.get().colorTruce; } },
	
	ALLY(
		"an ally", "allies", "an allied faction", "allied factions",
		"Ally"
	) { @Override public ChatColor getColor() { return MConf.get().colorAlly; } },
	SISTER(
		"a sister", "sisters", "a sister faction", "sister factions",
		"Sister"
	) { @Override public ChatColor getColor() { return MConf.get().colorSister; } },
	
	RECRUIT(
		"a recruit in your faction", "recruits in your faction", "", "",
		"Recruit"
	) { @Override public String getPrefix() { return MConf.get().prefixRecruit; } },
	
	MEMBER(
		"a member in your faction", "members in your faction", "your faction", "your factions",
		"Member"
	) { @Override public String getPrefix() { return MConf.get().prefixMember; } },
	
	OFFICER(
		"an officer in your faction", "officers in your faction", "", "",
		"Officer", "Moderator"
	) { @Override public String getPrefix() { return MConf.get().prefixOfficer; } },
	
	COLEADER
	(
		"a coleader in your faction", "coleaders in your faction", "", "",
		"Coleader"
	) { @Override public String getPrefix() { return MConf.get().prefixColeader; } },
	
	LEADER(
		"your faction leader", "your faction leader", "", "",
		"Leader", "Admin", "Owner"
	) { @Override public String getPrefix() { return MConf.get().prefixLeader; } },
	
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
	@Override public String getName() { return this.getNames().iterator().next(); }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	Rel(String descPlayerOne, String descPlayerMany, String descFactionOne, String descFactionMany, String... names)
	{
		this.descPlayerOne = descPlayerOne;
		this.descPlayerMany = descPlayerMany;
		this.descFactionOne = descFactionOne;
		this.descFactionMany = descFactionMany;
		this.names = Collections.unmodifiableSet(new MassiveSet<String>(names));
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public ChatColor getColor()
	{
		return MConf.get().colorMember;
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
		

	public String getPrefix()
	{
		return "";
	}
	
}
