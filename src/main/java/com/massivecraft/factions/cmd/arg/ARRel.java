package com.massivecraft.factions.cmd.arg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Rel;
import com.massivecraft.massivecore.cmd.arg.ARAbstractSelect;
import com.massivecraft.massivecore.util.Txt;

public class ARRel extends ARAbstractSelect<Rel>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static ARRel i = new ARRel();
	public static ARRel get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String typename()
	{
		return "role";
	}

	@Override
	public Rel select(String str, CommandSender sender)
	{
		return Rel.parse(str);
	}

	@Override
	public Collection<String> altNames(CommandSender sender)
	{
		List<String> ret = new ArrayList<String>(); 
		
		for (Rel rel : Rel.values())
		{
			ret.add(Txt.getNicedEnum(rel));
		}
		
		return ret;
	}
	
}
