package com.massivecraft.factions.cmd.type;

import java.util.Set;

import com.massivecraft.factions.Rel;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.command.type.enumeration.TypeEnum;

public class TypeRank extends TypeEnum<Rel>
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public static final Set<String> NAMES_PROMOTE = new MassiveSet<String>(
		"Promote",
		"+",
		"Plus",
		"Up"
	);
	
	public static final Set<String> NAMES_DEMOTE = new MassiveSet<String>(
		"Demote",
		"-",
		"Minus",
		"Down"
	);

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	// Can be used to promote and demote.
	public static TypeRank get(Rel rank) { return new TypeRank(rank); }
	public TypeRank(Rel rank)
	{
		super(Rel.class);
		if (rank != null && ! rank.isRank()) throw new IllegalArgumentException(rank + " is not a valid rank");
		this.startRank = rank;
	}
	
	// Can not be used to promote and demote.
	private static TypeRank i = new TypeRank();
	public static TypeRank get() { return i; }
	public TypeRank()
	{
		this(null);
	}
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private Rel startRank;
	public Rel getStartRank() { return this.startRank; }
	public void setStartRank(Rel startRank) { this.startRank = startRank; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String getTypeName()
	{
		return "rank";
	}
	
	@Override
	public Set<String> getNamesInner(Rel value)
	{
		// Create
		Set<String> ret = new MassiveSet<String>(super.getNamesInner(value));
		
		// Fill Exact
		if (value == Rel.LEADER)
		{
			ret.add("admin");
		}
		else if (value == Rel.OFFICER)
		{
			ret.add("moderator");
		}
		else if (value == Rel.MEMBER)
		{
			ret.add("member");
			ret.add("normal");
		}
		
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
