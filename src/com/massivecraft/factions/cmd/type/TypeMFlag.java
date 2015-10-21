package com.massivecraft.factions.cmd.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.massivecore.cmd.type.TypeAbstractSelect;
import com.massivecraft.massivecore.cmd.type.TypeAllAble;
import com.massivecraft.massivecore.util.Txt;

public class TypeMFlag extends TypeAbstractSelect<MFlag> implements TypeAllAble<MFlag>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TypeMFlag i = new TypeMFlag();
	public static TypeMFlag get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String getTypeName()
	{
		return "faction flag";
	}

	@Override
	public MFlag select(String arg, CommandSender sender)
	{
		if (arg == null) return null;
		arg = getComparable(arg);
		
		// Algorithmic General Detection
		int startswithCount = 0;
		MFlag startswith = null;
		for (MFlag mflag : MFlag.getAll())
		{
			String comparable = getComparable(mflag);
			if (comparable.equals(arg)) return mflag;
			if (comparable.startsWith(arg))
			{
				startswith = mflag;
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
		
		for (MFlag mflag : MFlag.getAll())
		{
			ret.add(Txt.upperCaseFirst(mflag.getName()));
		}
		
		return ret;
	}

	@Override
	public Collection<String> getTabList(CommandSender sender, String arg)
	{
		return this.altNames(sender);
	}

	@Override
	public Collection<MFlag> getAll(CommandSender sender)
	{
		return MFlag.getAll();
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public static String getComparable(String string)
	{
		return string.toLowerCase();
	}
	
	public static String getComparable(MFlag mflag)
	{
		return getComparable(mflag.getName());
	}

}
