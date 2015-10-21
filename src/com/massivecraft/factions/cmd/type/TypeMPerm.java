package com.massivecraft.factions.cmd.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.cmd.type.TypeAbstractSelect;
import com.massivecraft.massivecore.cmd.type.TypeAllAble;
import com.massivecraft.massivecore.util.Txt;

public class TypeMPerm extends TypeAbstractSelect<MPerm> implements TypeAllAble<MPerm>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TypeMPerm i = new TypeMPerm();
	public static TypeMPerm get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String getTypeName()
	{
		return "faction permission";
	}

	@Override
	public MPerm select(String arg, CommandSender sender)
	{
		if (arg == null) return null;
		arg = getComparable(arg);
		
		// Algorithmic General Detection
		int startswithCount = 0;
		MPerm startswith = null;
		for (MPerm mperm : MPerm.getAll())
		{
			String comparable = getComparable(mperm);
			if (comparable.equals(arg)) return mperm;
			if (comparable.startsWith(arg))
			{
				startswith = mperm;
				startswithCount++;
			}
		}
		
		if (startswithCount == 1)
		{
			return startswith;
		}
		
		// Nothing found
		return null;
	}

	@Override
	public Collection<String> altNames(CommandSender sender)
	{
		List<String> ret = new ArrayList<String>(); 
		
		for (MPerm mperm : MPerm.getAll())
		{
			ret.add(Txt.upperCaseFirst(mperm.getName()));
		}
		
		return ret;
	}

	@Override
	public Collection<String> getTabList(CommandSender sender, String arg)
	{
		return this.altNames(sender);
	}

	@Override
	public Collection<MPerm> getAll(CommandSender sender)
	{
		return MPerm.getAll();
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public static String getComparable(String string)
	{
		return string.toLowerCase();
	}
	
	public static String getComparable(MPerm mperm)
	{
		return getComparable(mperm.getName());
	}

}
