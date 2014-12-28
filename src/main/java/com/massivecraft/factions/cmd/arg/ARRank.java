package com.massivecraft.factions.cmd.arg;

import java.util.Collection;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Rel;
import com.massivecraft.massivecore.cmd.arg.ARAbstractSelect;
import com.massivecraft.massivecore.mixin.Mixin;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;

public class ARRank extends ARAbstractSelect<Rel>
{
	//----------------------------------------------//
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	//Default constructor. Can't use promote and demote.
	private static ARRank i = new ARRank();
	public static ARRank get() { return i; }
	
	public ARRank()
	{
		this.startRank = null;
	}
	
	//Fancy constructor. Can use promote and demote
	public static ARRank get(Rel rank) { return new ARRank(rank); }
	
	public ARRank(Rel rank)
	{
		this.startRank = rank;
	}
	
	//----------------------------------------------//
	// FIELDS
	// -------------------------------------------- //
	
	final Rel startRank;
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String typename()
	{
		return "rank";
	}

	@Override
	public Rel select(String arg, CommandSender sender)
	{
		//Default it is nothing
		Rel ret = null;
		
		// This is especially useful when one rank can have aliases.
		// In the case of promote/demote, 
		// that would require 10 lines of code repeated for each alias.
		arg = this.prepareArg(arg);
		
		switch(arg)
		{
			// All the normal ranks
			case "leader": ret = Rel.LEADER; break;
			case "officer": ret = Rel.OFFICER; break;
			case "member": ret = Rel.MEMBER; break;
			case "recruit": ret = Rel.RECRUIT; break;
			
			// Promote
			case "promote":
				switch(startRank)
				{
					case LEADER : ret = Rel.LEADER; break;
					case OFFICER : ret = Rel.LEADER; break;
					case MEMBER : ret = Rel.OFFICER; break;
					case RECRUIT : ret = Rel.MEMBER; break;
					// This should not happen
					default:
						//This might happen of the default constrcutor is used
						Mixin.msgOne(sender, Txt.parse("<b>You can't use promote & demote"));
						ret = null; break;
			
				} break;
			
			// Demote
			case "demote": 
				switch(startRank)
				{
					case LEADER : ret = Rel.OFFICER; break;
					case OFFICER : ret = Rel.MEMBER; break;
					case MEMBER : ret = Rel.RECRUIT; break;
					case RECRUIT : ret = Rel.RECRUIT; break;
					// This should not happen
					default:
						//This might happen of the default constrcutor is used
						Mixin.msgOne(sender, Txt.parse("<b>You can't use promote & demote"));
						ret = null; break;
				} break;
		}
		
		return ret;
	}

	@Override
	public Collection<String> altNames(CommandSender sender)
	{
		return MUtil.list(
				Txt.getNicedEnum(Rel.LEADER),
				Txt.getNicedEnum(Rel.OFFICER),
				Txt.getNicedEnum(Rel.MEMBER),
				Txt.getNicedEnum(Rel.RECRUIT),
				"Promote",
				"Demote");
	}
	
	// -------------------------------------------- //
	// PRIVATE
	// -------------------------------------------- //
	
	private String prepareArg(String str)
	{
		String ret = str;
		
		if (str.startsWith("admin") || str.startsWith("lea"))
		{
			ret = "leader";
		}
		else if (str.startsWith("mod") || str.startsWith("off"))
		{
			ret = "officer";
		}
		else if (str.startsWith("mem"))
		{
			ret = "member";
		}
		else if (str.startsWith("rec"))
		{
			ret = "recruit";
		}
		else if (str.startsWith("+") || str.startsWith("plus"))
		{
			ret = "promote";
		}
		else if (str.startsWith("-") || str.startsWith("minus"))
		{
			ret = "demote";
		}
		
		return ret;
	}
}
