package com.massivecraft.factions.cmd.arg;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Rel;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.cmd.arg.ARAbstractSelect;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;

public class ARRank extends ARAbstractSelect<Rel>
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public static final List<String> ALT_NAMES = Collections.unmodifiableList(MUtil.list(
			Txt.getNicedEnum(Rel.LEADER),
			Txt.getNicedEnum(Rel.OFFICER),
			Txt.getNicedEnum(Rel.MEMBER),
			Txt.getNicedEnum(Rel.RECRUIT),
			"Promote",
			"Demote"
		));
	
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
		if (rank == null) throw new IllegalArgumentException("Do not use null, the default constructor can be used however.");
		if ( ! rank.isRank()) throw new IllegalArgumentException(rank + " is not a valid rank");
		this.startRank = rank;
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
	public Rel select(String arg, CommandSender sender) throws MassiveException
	{
		// This is especially useful when one rank can have aliases.
		// In the case of promote/demote, 
		// that would require 10 lines of code repeated for each alias.
		arg = getComparable(arg);
		
		// All the normal ranks
		if (arg.equals("leader")) return Rel.LEADER;
		if (arg.equals("officer")) return Rel.OFFICER;
		if (arg.equals("member")) return Rel.MEMBER;
		if (arg.equals("recruit")) return Rel.RECRUIT;
		
		// No start rank?
		if (startRank == null && (arg.equals("promote") || arg.equals("demote")))
		{
			// This might happen if the default constructor is used
			throw new MassiveException().addMsg("<b>You can't use promote & demote.");
		}
		
		// Promote
		if (arg.equals("promote"))
		{
			if (Rel.LEADER.equals(startRank)) throw new MassiveException().addMsg("<b>You can't promote the leader.");
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
			if (Rel.RECRUIT.equals(startRank)) throw new MassiveException().addMsg("<b>You can't demote a recruit.");
		}
		
		return null;
	}

	@Override
	public Collection<String> altNames(CommandSender sender)
	{
		return ALT_NAMES;
	}


	@Override
	public Collection<String> getTabList(CommandSender sender, String arg)
	{
		return this.altNames(sender);
	}
	
	@Override
	public boolean isValid(String arg, CommandSender sender)
	{
		try
		{
			return this.select(arg, sender) != null;
		}
		catch (MassiveException e)
		{
			return true;
		}
	}
	
	// -------------------------------------------- //
	// ARG
	// -------------------------------------------- //
	
	public static String getComparable(String str)
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
		else if (ret.startsWith("mem") || ret.startsWith("nor"))
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
