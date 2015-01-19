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
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	// Default constructor. Can't use promote and demote.
	private static ARRank i = new ARRank();
	public static ARRank get() { return i; }
	
	public ARRank()
	{
		this.startRank = null;
	}
	
	// Fancy constructor. Can use promote and demote.
	public static ARRank get(Rel rank) { return new ARRank(rank); }
	
	public ARRank(Rel rank)
	{
		this.startRank = rank;
	}
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final Rel startRank;
	public Rel getStartRank() { return this.startRank; }
	
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
		// This is especially useful when one rank can have aliases.
		// In the case of promote/demote, 
		// that would require 10 lines of code repeated for each alias.
		arg = this.prepareArg(arg);
		
		// All the normal ranks
		if (arg.equals("leader")) return Rel.LEADER;
		if (arg.equals("officer")) return Rel.OFFICER;
		if (arg.equals("member")) return Rel.MEMBER;
		if (arg.equals("recruit")) return Rel.RECRUIT;
		
		// No start rank?
		if (startRank == null)
		{
			// This might happen of the default constructor is used
			Mixin.msgOne(sender, Txt.parse("<b>You can't use promote & demote"));
			return null;
		}
		
		// Promote
		if (arg.equals("promote"))
		{
			if (Rel.LEADER.equals(startRank)) return Rel.LEADER;
			if (Rel.OFFICER.equals(startRank)) return Rel.LEADER;
			if (Rel.MEMBER.equals(startRank)) return Rel.OFFICER;
			if (Rel.RECRUIT.equals(startRank)) return Rel.MEMBER;
		}
		
		// Demote
		if (arg.equals("demote"))
		{
			if (Rel.LEADER.equals(startRank)) return Rel.OFFICER;
			if (Rel.OFFICER.equals(startRank)) return Rel.MEMBER;
			if (Rel.MEMBER.equals(startRank)) return Rel.RECRUIT;
			if (Rel.RECRUIT.equals(startRank)) return Rel.RECRUIT;
		}
		
		return null;
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
			"Demote"
		);
	}
	
	// -------------------------------------------- //
	// PRIVATE
	// -------------------------------------------- //
	
	private String prepareArg(String str)
	{
		String ret = str.toLowerCase();
		
		if (ret.startsWith("admin") || ret.startsWith("lea"))
		{
			ret = "leader";
		}
		else if (ret.startsWith("mod") || ret.startsWith("off"))
		{
			ret = "officer";
		}
		else if (ret.startsWith("mem"))
		{
			ret = "member";
		}
		else if (ret.startsWith("rec"))
		{
			ret = "recruit";
		}
		else if (ret.startsWith("+") || ret.startsWith("plus") || ret.startsWith("up"))
		{
			ret = "promote";
		}
		else if (ret.startsWith("-") || ret.startsWith("minus") || ret.startsWith("down"))
		{
			ret = "demote";
		}
		
		return ret;
	}
}
