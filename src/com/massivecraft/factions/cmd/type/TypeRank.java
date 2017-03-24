package com.massivecraft.factions.cmd.type;

import com.massivecraft.factions.Rel;
import com.massivecraft.massivecore.collections.MassiveMap;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.command.type.enumeration.TypeEnum;
import com.massivecraft.massivecore.util.MUtil;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TypeRank extends TypeEnum<Rel>
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public static final Set<String> NAMES_PROMOTE = new MassiveSet<>(
		"Promote",
		"+",
		"Plus",
		"Up"
	);
	
	public static final Set<String> NAMES_DEMOTE = new MassiveSet<>(
		"Demote",
		"-",
		"Minus",
		"Down"
	);

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	// Because of the caching in TypeAbstractChoice, we want only one of each instance.
	
	// Null instance, doesn't allow promote and demote.
	private static final TypeRank i = new TypeRank(null);
	public static TypeRank get() { return i; }
	
	// Cached instances, does allow promote and demote.
	private static final Map<Rel, TypeRank> instances;
	static
	{
		Map<Rel, TypeRank> result = new MassiveMap<>();
		for (Rel rel : Rel.values())
		{
			if ( ! rel.isRank()) continue;
			result.put(rel, new TypeRank(rel));
		}
		result.put(null, i);
		instances = Collections.unmodifiableMap(result);
	}
	public static TypeRank get(Rel rank) { return instances.get(rank); }
	
	// Constructor
	public TypeRank(Rel rank)
	{
		super(Rel.class);
		if (rank != null && ! rank.isRank()) throw new IllegalArgumentException(rank + " is not a valid rank");
		this.startRank = rank;
		
		// Do setAll with only ranks.
		List<Rel> all = MUtil.list(Rel.values());
		for (Iterator<Rel> it = all.iterator(); it.hasNext(); )
		{
			if ( ! it.next().isRank()) it.remove();
		}
		
		this.setAll(all);
	}
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	// This must be final, for caching in TypeAbstractChoice to work.
	private final Rel startRank;
	public Rel getStartRank() { return this.startRank; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String getName()
	{
		return "rank";
	}
	
	@Override
	public String getNameInner(Rel value)
	{
		return value.getName();
	}
	
	@Override
	public Set<String> getNamesInner(Rel value)
	{
		// Create
		Set<String> ret = new MassiveSet<>();
		
		// Fill Exact
		ret.addAll(value.getNames());
		
		// Fill Relative
		Rel start = this.getStartRank();
		if (start != null)
		{
			if (value == Rel.LEADER && start == Rel.OFFICER) ret.addAll(NAMES_PROMOTE);
			
			if (value == Rel.OFFICER && start == Rel.MEMBER) ret.addAll(NAMES_PROMOTE);
			if (value == Rel.OFFICER && start == Rel.LEADER) ret.addAll(NAMES_DEMOTE);
			
			if (value == Rel.MEMBER && start == Rel.RECRUIT) ret.addAll(NAMES_PROMOTE);
			if (value == Rel.MEMBER && start == Rel.OFFICER) ret.addAll(NAMES_DEMOTE);
			
			if (value == Rel.RECRUIT && start == Rel.MEMBER) ret.addAll(NAMES_DEMOTE);
		}
		
		// Return
		return ret;
	}

}
