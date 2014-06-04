package com.massivecraft.factions.cmd.arg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.FFlag;
import com.massivecraft.massivecore.cmd.arg.ARAbstractSelect;
import com.massivecraft.massivecore.util.Txt;

public class ARFFlag extends ARAbstractSelect<FFlag>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static ARFFlag i = new ARFFlag();
	public static ARFFlag get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String typename()
	{
		return "faction flag";
	}

	@Override
	public FFlag select(String str, CommandSender sender)
	{
		return FFlag.parse(str);
	}

	@Override
	public Collection<String> altNames(CommandSender sender)
	{
		List<String> ret = new ArrayList<String>(); 
		
		for (FFlag fflag : FFlag.values())
		{
			ret.add(Txt.getNicedEnum(fflag));
		}
		
		return ret;
	}
	
}
