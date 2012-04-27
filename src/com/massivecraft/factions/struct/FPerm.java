package com.massivecraft.factions.struct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.iface.RelationParticipator;

/**
 * Permissions that you (a player) may or may not have in the territory of a certain faction.
 * Each faction have many Rel's assigned to each one of these Perms. 
 */
public enum FPerm
{
	BUILD("build", "edit the terrain",             Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY),
	PAINBUILD("painbuild", "edit but take damage"),
	DOOR("door", "use doors",                      Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY),
	BUTTON("button", "use stone buttons",          Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY),
	LEVER("lever", "use levers",                   Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY),
	CONTAINER("container", "use containers",       Rel.LEADER, Rel.OFFICER, Rel.MEMBER),
	KICK("kick", "kick members",                   Rel.LEADER, Rel.OFFICER),
	SETHOME("sethome", "set the home",             Rel.LEADER, Rel.OFFICER),
	WITHDRAW("withdraw", "withdraw money",         Rel.LEADER, Rel.OFFICER),
	TERRITORY("territory", "claim or unclaim",     Rel.LEADER, Rel.OFFICER),
	DISBAND("disband", "disband the faction",      Rel.LEADER),
	PERMS("perms", "manage permissions",           Rel.LEADER),
	;
	
	private final String nicename;
	private final String desc;
	public final Set<Rel> defaultDefaultValue;
	
	private FPerm(final String nicename, final String desc, final Rel... rels)
	{
		this.nicename = nicename;
		this.desc = desc;
		this.defaultDefaultValue = new HashSet<Rel>();
		this.defaultDefaultValue.addAll(Arrays.asList(rels));
	}
	
	public String getNicename()
	{
		return this.nicename;
	}
	
	public String getDescription()
	{
		return this.desc;
	}
	
	public Set<Rel> getDefault()
	{
		Set<Rel> ret = Conf.factionPermDefaults.get(this);
		if (ret == null) return this.defaultDefaultValue;
		return ret; 
	}
	
	public static FPerm parse(String str)
	{
		str = str.toLowerCase();
		if (str.startsWith("bui")) return BUILD;
		if (str.startsWith("pa"))  return PAINBUILD;
		if (str.startsWith("do"))  return DOOR;
		if (str.startsWith("but")) return BUTTON;
		if (str.startsWith("l"))   return LEVER;
		if (str.startsWith("co"))  return CONTAINER;
		if (str.startsWith("k"))   return KICK;
		if (str.startsWith("s"))   return SETHOME;
		if (str.startsWith("w"))   return WITHDRAW;
		if (str.startsWith("t"))   return TERRITORY;
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
	
	public static Set<Rel> parseRelDeltas(String str, Set<Rel> current)
	{
		Set<Rel> ret = new HashSet<Rel>();
		ret.addAll(current);
		
		List<String> nodes = new ArrayList<String>(Arrays.asList(str.split("\\s+")));
		
		for (String node : nodes)
		{
			boolean add = true;
			if (node.startsWith("-"))
			{
				add = false;
				node = node.substring(1);
			}
			else if (node.startsWith("+"))
			{
				node = node.substring(1);
			}
			Rel rel = Rel.parse(node);
			
			if (rel == null) continue;
			
			if (add)
			{
				ret.add(rel);
			}
			else
			{
				ret.remove(rel);
			}
		}
		return ret; 
	}
	
	private static final String errorpattern = "%s<b> does not allow you to %s<b>.";
	public boolean has(Object testSubject, Faction hostFaction, boolean informIfNot)
	{
		if (testSubject instanceof ConsoleCommandSender) return true;
		
		RelationParticipator rpSubject = null;
		
		if (testSubject instanceof Player)
		{
			rpSubject = FPlayers.i.get((Player)testSubject);
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
		
		if (rpSubject instanceof FPlayer && ret == false && ((FPlayer)rpSubject).hasAdminMode()) ret = true;
		
		if (!ret && informIfNot && rpSubject instanceof FPlayer)
		{
			FPlayer fplayer = (FPlayer)rpSubject;
			fplayer.msg(errorpattern, hostFaction.describeTo(fplayer, true), this.getDescription());
			if (Permission.ADMIN.has(fplayer.getPlayer()))
			{
				fplayer.msg("<i>You can bypass by using " + P.p.cmdBase.cmdBypass.getUseageTemplate(false));
			}
		}
		return ret;
	}
	public boolean has(Object testSubject, Faction hostFaction)
	{
		return this.has(testSubject, hostFaction, false);
	}
	public boolean has(Object testSubject, FLocation floc, boolean informIfNot)
	{
		Faction factionThere = Board.getFactionAt(floc);
		return this.has(testSubject, factionThere, informIfNot);
	}
	public boolean has(Object testSubject, Location loc, boolean informIfNot)
	{
		FLocation floc = new FLocation(loc);
		return this.has(testSubject, floc, informIfNot);
	}
	public boolean has(Object testSubject, Location loc)
	{
		return this.has(testSubject, loc, false);
	}
	public boolean has(Object testSubject, FLocation floc)
	{
		return this.has(testSubject, floc, false);
	}
}
