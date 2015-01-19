package com.massivecraft.factions.cmd.arg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.massivecore.cmd.arg.ARAbstractSelect;
import com.massivecraft.massivecore.util.Txt;

public class ARMFlag extends ARAbstractSelect<MFlag>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static ARMFlag i = new ARMFlag();
	public static ARMFlag get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String typename()
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
