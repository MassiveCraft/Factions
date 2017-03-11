package com.massivecraft.factions.cmd.type;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Rank;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.command.type.TypeAbstract;
import com.massivecraft.massivecore.mson.Mson;

public class TypeRank extends TypeAbstract<Rank>
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public static final Set<String> NAMES_PROMOTE = new MassiveSet<>(
		"Promote",
		"+",
		"Plus",
		"Up"
	);
	
	public static final Set<String> NAMES_DEMOTE = new MassiveSet<>(
		"Demote",
		"-",
		"Minus",
		"Down"
	);
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final Rank startRank;
	public Rank getStartRank() { return this.startRank; }
	
	private Faction faction;
	public Faction getFaction() { return this.faction; }
	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	// Because of the caching in TypeAbstractChoice, we want only one of each instance.
	
	// Null instance, doesn't allow promote and demote.
	private static final TypeRank i = new TypeRank(null, null);
	public static TypeRank get() { return i; }
	
	// Constructor
	public TypeRank(Rank rank, Faction faction)
	{
		super(Rank.class);
		
		this.startRank = rank;
		this.faction = faction;
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Set<String> getNamesInner(Rank rank)
	{
		// Create
		Set<String> ret = new MassiveSet<String>();
		
		// Fill Relative
		Rank start = this.getStartRank();
		if (start != null)
		{
			if (start.isHigherThan(rank)) ret.addAll(NAMES_PROMOTE);
			
			if (start.isLessThan(rank)) ret.addAll(NAMES_DEMOTE);
		}
		
		// Return
		return ret;
	}
	
	@Override
	public Rank read(String arg, CommandSender sender) throws MassiveException
	{
		return null;
	}
	
	@Override
	public Collection<String> getTabList(CommandSender sender, String arg)
	{
		return null;
	}
	
	@Override
	public Mson getVisualMson(Rank value)
	{
		// Create
		Mson mson = Mson.mson("[", value.getName(), "]");
		
		// Fill
		mson = mson.tooltip(Mson.toPlain(this.getShow(value), true));
		
		// Return
		return mson;
	}
	
	@Override
	public List<Mson> getShowInner(Rank value, CommandSender sender)
	{
		Mson order = getShowLine("Order", value.getOrder());
		Mson prefix = getShowLine("Prefix", value.getPrefix());
		
		return new MassiveList<>(order, prefix);
	}
	
	private static Mson getShowLine(String key, Object value)
	{
		return Mson.mson(
				Mson.mson(key).color(ChatColor.AQUA),
				Mson.mson(":").color(ChatColor.GRAY),
				Mson.SPACE,
				value.toString()
		).color(ChatColor.YELLOW);
	}
}
