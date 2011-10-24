package com.massivecraft.factions.struct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.util.RelationUtil;

/**
 * Permissions that you (a player) may or may not have in the territory of a certain faction.
 * Each faction have many Rel's assigned to each one of these Perms. 
 */
public enum FPerm
{
	BUILD("build", "edit the terrain", Rel.LEADER, Rel.OFFICER, Rel.MEMBER),
	PAINBUILD("painbuild", "edit but take damage", Rel.ALLY),
	DOOR("door", "use doors", Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.ALLY),
	CONTAINER("container", "use containers", Rel.LEADER, Rel.OFFICER, Rel.MEMBER),
	BUTTON("button", "use stone buttons", Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.ALLY),
	LEVER("lever", "use levers", Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.ALLY),
	WITHDRAW("withdraw", "withdraw faction money", Rel.LEADER, Rel.OFFICER),
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
		if (str.startsWith("p")) return PAINBUILD;
		if (str.startsWith("d")) return DOOR;
		if (str.startsWith("c")) return CONTAINER;
		if (str.startsWith("but")) return BUTTON;
		if (str.startsWith("l")) return LEVER;
		if (str.startsWith("w")) return WITHDRAW;
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
		
		ret +="<h>"+this.getNicename();
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
	
	private static final String errorpattern = "<b>%s<b> can't %s in the territory of %s<b>.";
	public boolean has(RelationParticipator testSubject, Faction hostFaction, boolean informIfNot)
	{
		Faction factionDoer = RelationUtil.getFaction(testSubject);
		boolean ret = hostFaction.getPermittedRelations(this).contains(hostFaction.getRelationTo(factionDoer));
		if (!ret && informIfNot && testSubject instanceof FPlayer)
		{
			FPlayer fplayer = (FPlayer)testSubject;
			fplayer.msg(errorpattern, fplayer.describeTo(fplayer, true), this.getDescription(), hostFaction.describeTo(fplayer));
		}
		return ret;
	}
	public boolean has(RelationParticipator testSubject, Faction hostFaction)
	{
		return this.has(testSubject, hostFaction, false);
	}
	public boolean has(RelationParticipator testSubject, FLocation floc, boolean informIfNot)
	{
		Faction factionThere = Board.getFactionAt(floc);
		return this.has(testSubject, factionThere, informIfNot);
	}
	public boolean has(RelationParticipator testSubject, Location loc, boolean informIfNot)
	{
		FLocation floc = new FLocation(loc);
		return this.has(testSubject, floc, informIfNot);
	}
	public boolean has(RelationParticipator testSubject, Location loc)
	{
		return this.has(testSubject, loc, false);
	}
	public boolean has(RelationParticipator testSubject, FLocation floc)
	{
		return this.has(testSubject, floc, false);
	}
}
