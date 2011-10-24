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
import com.massivecraft.factions.zcore.util.TextUtil;

/**
 * Permissions that you (a player) may or may not have in the territory of a certain faction.
 * Each faction have many Rel's assigned to each one of these Perms. 
 */
public enum FPerm
{
	BUILD("build", "edit the terrain", Rel.MEMBER),
	PAINBUILD("painbuild", "edit but take damage", Rel.ALLY),
	DOOR("door", "use doors", Rel.MEMBER, Rel.ALLY),
	CONTAINER("container", "use containers", Rel.MEMBER),
	BUTTON("button", "use stone buttons", Rel.MEMBER, Rel.ALLY),
	LEVER("lever", "use levers", Rel.MEMBER, Rel.ALLY),
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
		return null;
	}
	
	public String getStateInfo(Set<Rel> value, boolean withDesc)
	{
		String ret = "<h>"+this.getNicename()+ " ";
		
		List<String> rels = new ArrayList<String>();
		for (Rel rel : value)
		{
			rels.add("<p>"+rel);
		}
		if (rels.size() > 0)
		{
			ret += TextUtil.implode(rels, "<c>+");
		}
		else
		{
			ret += "NOONE";
		}
		
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
	public boolean has(RelationParticipator testSubject, FLocation floc, boolean informIfNot)
	{
		Faction factionThere = Board.getFactionAt(floc);
		Faction factionDoer = RelationUtil.getFaction(testSubject);
		boolean ret = factionThere.getPermittedRelations(this).contains(factionThere.getRelationTo(factionDoer));
		if (!ret && informIfNot && testSubject instanceof FPlayer)
		{
			FPlayer fplayer = (FPlayer)testSubject;
			fplayer.msg(errorpattern, fplayer.describeTo(fplayer, true), this.getDescription(), factionThere.describeTo(fplayer));
		}
		return ret;
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
