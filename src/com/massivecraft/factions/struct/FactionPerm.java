package com.massivecraft.factions.struct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.zcore.util.TextUtil;

/**
 * Permissions that you (a player) may or may not have in the territory of a certain faction.
 * Each faction have many Rel's assigned to each one of these Perms. 
 */
public enum FactionPerm
{
	BUILD("build", "edit the terrain", Rel.MEMBER),
	PAINBUILD("painbuild", "edit but take damage", Rel.ALLY),
	DOOR("door", "use doors etc.", Rel.MEMBER, Rel.ALLY),
	CONTAINER("container", "use chests etc.", Rel.MEMBER),
	BUTTON("button", "use stone buttons", Rel.MEMBER, Rel.ALLY),
	LEVER("lever", "use levers", Rel.MEMBER, Rel.ALLY),
	;
	
	private final String nicename;
	private final String desc;
	public final Set<Rel> defaultDefaultValue;
	
	private FactionPerm(final String nicename, final String desc, final Rel... rels)
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
	
	public static FactionPerm parse(String str)
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
			rels.add("<i>"+rel.nicename);
		}
		ret += TextUtil.implode(rels, "<n> ,");
		
		if (withDesc)
		{
			ret += " " + this.getDescription();
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
}
