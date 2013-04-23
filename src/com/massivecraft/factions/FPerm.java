package com.massivecraft.factions;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.mcore.ps.PS;

/**
 * Permissions that you (a player) may or may not have in the territory of a certain faction.
 * Each faction have many Rel's assigned to each one of these Perms. 
 */
public enum FPerm
{
	// -------------------------------------------- //
	// ENUM
	// -------------------------------------------- //
	
	BUILD("build", "edit the terrain",             Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY),
	PAINBUILD("painbuild", "edit but take damage"),
	DOOR("door", "use doors",                      Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY),
	BUTTON("button", "use stone buttons",          Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY),
	LEVER("lever", "use levers",                   Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY),
	CONTAINER("container", "use containers",       Rel.LEADER, Rel.OFFICER, Rel.MEMBER),
	
	INVITE("invite", "invite players",             Rel.LEADER, Rel.OFFICER),
	KICK("kick", "kick members",                   Rel.LEADER, Rel.OFFICER),
	SETHOME("sethome", "set the home",             Rel.LEADER, Rel.OFFICER),
	WITHDRAW("withdraw", "withdraw money",         Rel.LEADER, Rel.OFFICER),
	TERRITORY("territory", "claim or unclaim",     Rel.LEADER, Rel.OFFICER),
	CAPE("cape", "set the cape",                   Rel.LEADER, Rel.OFFICER),
	ACCESS("access", "grant territory access",     Rel.LEADER, Rel.OFFICER),
	DISBAND("disband", "disband the faction",      Rel.LEADER),
	PERMS("perms", "manage permissions",           Rel.LEADER),
	
	// END OF LIST
	;
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final String nicename;
	public String getNicename() { return this.nicename; }
	
	private final String desc;
	public String getDescription() { return this.desc; }
	
	public final Set<Rel> defaultDefault;
	public Set<Rel> getDefaultDefault() { return new LinkedHashSet<Rel>(this.defaultDefault); }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	private FPerm(final String nicename, final String desc, final Rel... rels)
	{
		this.nicename = nicename;
		this.desc = desc;
		
		Set<Rel> defaultDefaultValue = new LinkedHashSet<Rel>();
		defaultDefaultValue.addAll(Arrays.asList(rels));
		defaultDefaultValue = Collections.unmodifiableSet(defaultDefaultValue);
		this.defaultDefault = defaultDefaultValue;
	}
	
	// -------------------------------------------- //
	// DEFAULTS
	// -------------------------------------------- //
	
	public Set<Rel> getDefault(Object o)
	{
		Set<Rel> ret = UConf.get(o).defaultFactionPerms.get(this);
		if (ret == null) return this.getDefaultDefault();
		ret = new LinkedHashSet<Rel>(ret);
		return ret;
	}
	
	public static Map<FPerm, Set<Rel>> getDefaultDefaults()
	{
		Map<FPerm, Set<Rel>> ret = new LinkedHashMap<FPerm, Set<Rel>>();
		for (FPerm fperm : values())
		{
			ret.put(fperm, fperm.getDefaultDefault());
		}
		return ret;
	}
	
	// -------------------------------------------- //
	// FROOODLDLLD
	// -------------------------------------------- //

	public static FPerm parse(String str)
	{
		str = str.toLowerCase();
		if (str.startsWith("a"))   return ACCESS;
		if (str.startsWith("bui")) return BUILD;
		if (str.startsWith("pa"))  return PAINBUILD;
		if (str.startsWith("do"))  return DOOR;
		if (str.startsWith("but")) return BUTTON;
		if (str.startsWith("l"))   return LEVER;
		if (str.startsWith("co"))  return CONTAINER;
		if (str.startsWith("i"))   return INVITE;
		if (str.startsWith("k"))   return KICK;
		if (str.startsWith("s"))   return SETHOME;
		if (str.startsWith("w"))   return WITHDRAW;
		if (str.startsWith("t"))   return TERRITORY;
		if (str.startsWith("ca"))  return CAPE;
		if (str.startsWith("di"))  return DISBAND;
		if (str.startsWith("pe"))  return PERMS;
		return null;
	}
	
	public static String getStateHeaders()
	{
		String ret = "";
		for (Rel rel : Rel.values())
		{
			ret += rel.getColor().toString();
			ret += rel.toString().substring(0, 3);
			ret += " ";
		}
		
		return ret;
	}
	
	public String getStateInfo(Set<Rel> value, boolean withDesc)
	{
		String ret = "";
		
		for (Rel rel : Rel.values())
		{
			if (value.contains(rel))
			{
				ret += "<g>YES";
			}
			else
			{
				ret += "<b>NOO";
			}
			ret += " ";
		}
		
		ret +="<c>"+this.getNicename();
		if (withDesc)
		{
			ret += " <i>" + this.getDescription(); 
		}
		return ret;
	}

	// Perms which apply strictly to granting territory access
	// TODO: This should be a boolean field within the class itself!
	private static final Set<FPerm> TerritoryPerms = EnumSet.of(BUILD, DOOR, BUTTON, LEVER, CONTAINER);
	public boolean isTerritoryPerm()
	{
		return TerritoryPerms.contains(this);
	}

	private static final String errorpattern = "%s<b> does not allow you to %s<b>.";
	public boolean has(Object testSubject, Faction hostFaction, boolean informIfNot)
	{
		RelationParticipator rpSubject = null;
		
		if (testSubject instanceof CommandSender)
		{
			rpSubject = UPlayer.get(testSubject);
		}
		else if (testSubject instanceof RelationParticipator)
		{
			rpSubject = (RelationParticipator) testSubject;
		}
		else
		{
			return false;
		}
		
		Rel rel = rpSubject.getRelationTo(hostFaction);
		
		// TODO: Create better description messages like: "You must at least be officer".
		boolean ret = hostFaction.getPermittedRelations(this).contains(rel);
		
		if (rpSubject instanceof UPlayer && ret == false && ((UPlayer)rpSubject).isUsingAdminMode()) ret = true;
		
		if (!ret && informIfNot && rpSubject instanceof UPlayer)
		{
			UPlayer uplayer = (UPlayer)rpSubject;
			uplayer.msg(errorpattern, hostFaction.describeTo(uplayer, true), this.getDescription());
			if (Perm.ADMIN.has(uplayer.getPlayer()))
			{
				uplayer.msg("<i>You can bypass by using " + Factions.get().getOuterCmdFactions().cmdFactionsAdmin.getUseageTemplate(false));
			}
		}
		return ret;
	}
	public boolean has(Object testSubject, Faction hostFaction)
	{
		return this.has(testSubject, hostFaction, false);
	}
	public boolean has(Object testSubject, PS ps, boolean informIfNot)
	{
		TerritoryAccess access = BoardColls.get().getTerritoryAccessAt(ps);
		
		if (this.isTerritoryPerm())
		{
			if (access.subjectHasAccess(testSubject)) return true;
			if (access.subjectAccessIsRestricted(testSubject))
			{
				if (informIfNot)
				{
					UPlayer notify = null;
					if (testSubject instanceof CommandSender)
						notify = UPlayer.get(testSubject);
					else if (testSubject instanceof UPlayer)
						notify = (UPlayer)testSubject;
					if (notify != null)
						notify.msg("<b>This territory owned by your faction has restricted access.");
				}
				return false;
			}
		}
		
		return this.has(testSubject, BoardColls.get().getFactionAt(ps), informIfNot);
	}
	public boolean has(Object testSubject, PS ps)
	{
		return this.has(testSubject, ps, false);
	}
}
