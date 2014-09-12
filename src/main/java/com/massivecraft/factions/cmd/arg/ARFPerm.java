package com.massivecraft.factions.cmd.arg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.FPerm;
import com.massivecraft.massivecore.cmd.arg.ARAbstractSelect;
import com.massivecraft.massivecore.util.Txt;

public class ARFPerm extends ARAbstractSelect<FPerm>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static ARFPerm i = new ARFPerm();
	public static ARFPerm get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String typename()
	{
		return "faction permission";
	}

	@Override
	public FPerm select(String str, CommandSender sender)
	{
		return FPerm.parse(str);
	}

	@Override
	public Collection<String> altNames(CommandSender sender)
	{
		List<String> ret = new ArrayList<String>(); 
		
		for (FPerm fperm : FPerm.values())
		{
			ret.add(Txt.getNicedEnum(fperm));
		}
		
		return ret;
	}
	
}
