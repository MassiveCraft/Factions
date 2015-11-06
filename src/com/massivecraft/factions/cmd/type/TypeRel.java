package com.massivecraft.factions.cmd.type;

import java.util.Set;

import com.massivecraft.factions.Rel;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.command.type.enumeration.TypeEnum;

public class TypeRel extends TypeEnum<Rel>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TypeRel i = new TypeRel();
	public static TypeRel get() { return i; }
	public TypeRel() { super(Rel.class); }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String getTypeName()
	{
		return "role";
	}
	
	@Override
	public Set<String> getNamesInner(Rel value)
	{
		Set<String> ret = new MassiveSet<String>(super.getNamesInner(value));
		
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
			ret.add("normal");
		}
		
		return ret;
	}
	
}
